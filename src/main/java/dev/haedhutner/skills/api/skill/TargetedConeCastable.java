package dev.haedhutner.skills.api.skill;

import com.flowpowered.math.imaginary.Quaterniond;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import dev.haedhutner.skills.api.exception.CastException;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.stream.Collectors;

public interface TargetedConeCastable extends TargetedCastable {
    @Override
    default CastResult cast(Living user, long timestamp, String... args) throws CastException {
        Collection<Living> nearby = user.getNearbyEntities(getRange(user)).stream()
                .filter(entity -> entity instanceof Living)
                .map(entity -> (Living) entity)
                .collect(Collectors.toList());

        if (nearby.isEmpty()) {
            throw CastErrors.noTarget();
        }

        BlockRay<World> blockRay = BlockRay.from(user).distanceLimit(getRange(user)).build();
        Set<Vector3i> locations = new HashSet<>();

        // Cast ray once so we know what blocks are visible
        while (blockRay.hasNext()) {
            BlockRayHit<World> blockRayHit = blockRay.next();
            BlockType blockType = blockRayHit.getExtent().getBlockType(blockRayHit.getBlockPosition());

            if (getPassableBlocks().contains(blockType.getId())) {
                break;
            }

            locations.add(blockRayHit.getBlockPosition());
        }


        final Vector3d rotation = user.getHeadRotation();
        final Vector3d axis = Quaterniond.fromAxesAnglesDeg(rotation.getX(), -rotation.getY(), rotation.getZ()).getDirection();
        final Vector3d userPosition = user.getLocation().getPosition();

        Set<Living> finalTargets = new HashSet<>();
        double differenceSquared = Double.MAX_VALUE;

        for (Living target : nearby) {
            Vector3d targetPosition = target.getLocation().getPosition();
            Vector3d between = targetPosition.sub(userPosition);

            double dot = axis.dot(between.normalize());

            if (dot > Math.cos(getAngle(user))) {
                double lengthSquared = between.lengthSquared();
                if (lengthSquared < differenceSquared) {
                    differenceSquared = lengthSquared;
                    finalTargets.add(target);
                }
            }
        }

        if (finalTargets.isEmpty()) {
            throw CastErrors.noTarget();
        }

        if (!isMultiTarget(user)) {
            return cast(user, finalTargets.stream().findAny().get(), timestamp, args);
        } else {
            List<CastResult> multicastResult = finalTargets.stream()
                    .map(target -> {
                        try {
                            return cast(user, target, timestamp, args);
                        } catch (CastException e) {
                            if (user instanceof MessageReceiver && e.getText() != null) {
                                ((MessageReceiver) user).sendMessage(e.getText());
                            }

                            return CastResult.empty();
                        }
                    })
                    .collect(Collectors.toList());

            return CastResult.concat(multicastResult);
        }
    }

    default double getAngle(Living user) {
        return 5;
    }

    default boolean isMultiTarget(Living user) {
        return false;
    }
}
