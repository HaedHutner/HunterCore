package dev.haedhutner.skills.model;

import dev.haedhutner.core.db.SpongeIdentifiable;
import dev.haedhutner.skills.api.resource.ResourceUser;
import org.spongepowered.api.entity.living.Living;

import javax.annotation.Nonnull;
import java.util.UUID;

public class EntityResourceUser implements SpongeIdentifiable, ResourceUser {

    private final UUID uuid;
    private double current;
    private double max;

    public EntityResourceUser(Living entity) {
        this.uuid = entity.getUniqueId();
    }

    @Nonnull
    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public void fill(double amount) {
        this.current += amount;
    }

    @Override
    public void fill() {
        this.current = max;
    }

    @Override
    public void drain(double amount) {
        this.current -= amount;
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public void setMax(double amount) {
        this.max = amount;
    }

    @Override
    public double getCurrent() {
        return current;
    }
}
