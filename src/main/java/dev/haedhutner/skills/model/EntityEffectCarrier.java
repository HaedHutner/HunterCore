package dev.haedhutner.skills.model;

import dev.haedhutner.core.db.SpongeIdentifiable;
import dev.haedhutner.skills.api.effect.Applyable;
import dev.haedhutner.skills.api.effect.ApplyableCarrier;
import org.spongepowered.api.entity.living.Living;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class EntityEffectCarrier implements SpongeIdentifiable, ApplyableCarrier<Living> {

    private final Living cachedLiving;

    private final Set<Applyable> effects = new HashSet<>();

    public EntityEffectCarrier(Living living) {
        this.cachedLiving = living;
    }

    @Override
    public Optional<Living> getLiving() {
        if (cachedLiving == null) {
            return Optional.empty();
        }

        if (cachedLiving.isRemoved() || !cachedLiving.isLoaded()) {
            return Optional.empty();
        }

        return Optional.of(cachedLiving);
    }

    @Override
    public void addEffect(Applyable applyable) {
        effects.add(applyable);
    }

    @Override
    public void removeEffect(Applyable applyable) {
        effects.remove(applyable);
    }

    @Override
    public boolean hasEffect(Applyable applyable) {
        return effects.contains(applyable);
    }

    @Override
    public boolean hasEffects() {
        return !effects.isEmpty();
    }

    @Override
    public Set<Applyable> getEffects() {
        return effects;
    }

    @Nonnull
    @Override
    public UUID getId() {
        return cachedLiving.getUniqueId();
    }
}
