package dev.haedhutner.skills.api.skill;

import dev.haedhutner.core.utils.CoreUtils;
import dev.haedhutner.skills.api.exception.CastException;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.EntityUniverse;

import java.util.Optional;
import java.util.Set;

public interface TargetedCastable extends Castable {

    @Override
    default CastResult cast(Living user, long timestamp, String... args) throws CastException {
        double range = getRange(user);

        Optional<EntityUniverse.EntityHit> entityHit = user.getWorld()
                .getIntersectingEntities(user, range, eHit -> {
                    return eHit.getEntity() instanceof Living && user != eHit.getEntity();
                })
                .stream()
                .reduce((accumulator, other) -> {
                    return accumulator.getDistance() < other.getDistance() ? accumulator : other;
                });

        if (!entityHit.isPresent()) throw CastErrors.noTarget();

        Living target = (Living) entityHit.get().getEntity();

        Optional<BlockRayHit<World>> end = BlockRay.from(user)
                .select(CoreUtils.createBlockFilter(getPassableBlocks()))
                .distanceLimit(entityHit.get().getDistance())
                .build()
                .end();

        if (!end.isPresent()) {
            return cast(user, target, timestamp, args);
        }

        throw CastErrors.noTarget();
    }

    CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException;

    default double getRange(Living user) {
        return 100.0;
    }

    Set<String> getPassableBlocks();

    void setPassableBlocks(Set<String> blockIds);
}
