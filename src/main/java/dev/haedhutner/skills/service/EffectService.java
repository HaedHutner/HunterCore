package dev.haedhutner.skills.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.skills.api.effect.Applyable;
import dev.haedhutner.skills.api.effect.ApplyableCarrier;
import dev.haedhutner.skills.model.EntityEffectCarrier;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import java.util.*;

@Singleton
public class EffectService {

    @Inject
    PluginContainer plugin;

    private Task task;

    private final Map<String, Applyable> effects = new HashMap<>();

    private final Map<UUID, ApplyableCarrier<?>> cache = new HashMap<>();

    EffectService() {
    }

    public void registerEffect(Applyable applyable) {
        effects.put(applyable.getId(), applyable);
    }

    public void registerEffects(Applyable... applyables) {
        for (Applyable applyable : applyables) {
            registerEffect(applyable);
        }
    }

    public Optional<Applyable> getEffectById(String id) {
        return Optional.ofNullable(effects.get(id));
    }

    public ApplyableCarrier<?> getOrCreateCarrier(Living entity) {
        if (cache.containsKey(entity.getUniqueId())) {
            return cache.get(entity.getUniqueId());
        } else {
            EntityEffectCarrier newCarrier = new EntityEffectCarrier(entity);
            cache.put(entity.getUniqueId(), newCarrier);
            return newCarrier;
        }
    }

    public void applyEffect(Living entity, Applyable applyable) {
        if (task == null) {
            task = Task.builder()
                    .name("effect-service-task")
                    .intervalTicks(1)
                    .execute(this::tick)
                    .submit(plugin);
        }

        getOrCreateCarrier(entity).addEffect(applyable);
    }

    public void applyEffect(Living entity, String effectId) {
        Applyable effect = effects.get(effectId);
        if (effect != null) {
            getOrCreateCarrier(entity).addEffect(effect);
        }
    }

    public boolean hasEffect(Living entity, Applyable applyable) {
        return getOrCreateCarrier(entity).hasEffect(applyable);
    }

    public boolean hasEffect(Living entity, String effectId) {
        return getOrCreateCarrier(entity).getEffects().stream().anyMatch(effect -> effectId.equals(effect.getId()));
    }

    public void removeEffect(Living entity, Applyable applyable) {
        getOrCreateCarrier(entity).removeEffect(applyable);
    }

    public void removeEffect(Living entity, String effectId) {
        Set<Applyable> effects = getOrCreateCarrier(entity).getEffects();

        effects.removeIf(effect -> effect.getId().equals(effectId));
    }

    public void clearEffects(Living entity) {
        ApplyableCarrier<?> carrier = getOrCreateCarrier(entity);

        carrier.getEffects().forEach(Applyable::setRemoved);
    }

    public void clearNegativeEffects(Living entity) {
        ApplyableCarrier<?> carrier = getOrCreateCarrier(entity);

        carrier.getEffects().forEach(applyable -> {
            if (!applyable.isPositive()) {
                applyable.setRemoved();
            }
        });
    }

    public void clearPositiveEffects(Living entity) {
        ApplyableCarrier<?> carrier = getOrCreateCarrier(entity);

        carrier.getEffects().forEach(applyable -> {
            if (applyable.isPositive()) {
                applyable.setRemoved();
            }
        });
    }

    public Optional<Applyable> getNamedEffect(String id) {
        return Optional.ofNullable(effects.get(id));
    }

    private void tick() {
        long timestamp = System.currentTimeMillis();

        cache.entrySet().removeIf(entry -> {
            if (entry.getValue().hasEffects()) {
                tickAllEffects(timestamp, entry.getValue());
                return false;
            } else {
                return true;
            }
        });
    }

    private void tickAllEffects(long timestamp, ApplyableCarrier<?> carrier) {
        carrier.getEffects().removeIf(effect -> tickEffect(timestamp, carrier, effect));
    }

    private boolean tickEffect(long timestamp, ApplyableCarrier<?> carrier, Applyable effect) {
        if (effect.canApply(timestamp, carrier)) {
            effect.apply(timestamp, carrier);
        }

        if (effect.canRemove(timestamp, carrier)) {
            effect.remove(timestamp, carrier);
            return true;
        }

        return false;
    }
}
