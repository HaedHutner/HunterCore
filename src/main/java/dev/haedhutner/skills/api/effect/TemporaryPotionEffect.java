package dev.haedhutner.skills.api.effect;

import dev.haedhutner.core.utils.CoreUtils;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;

/**
 * Wrapper around a {@link PotionEffect}
 */
public class TemporaryPotionEffect extends TemporaryEffect {

    private final PotionEffect effect;

    public TemporaryPotionEffect(String id, String name, PotionEffect effect, boolean isPositive) {
        super(id, name, effect.getDuration() * 50L, isPositive);
        this.effect = effect;
    }

    public static TemporaryPotionEffect of(PotionEffectType effectType, int duration, int amplifier, boolean isPositive) {
        return new TemporaryPotionEffect(
                "hunter:" + effectType.getId() + "_effect",
                effectType.getName(),
                PotionEffect.of(effectType, duration, amplifier),
                isPositive
        );
    }

    @Override
    protected boolean apply(ApplyableCarrier<?> character) {
        return character.getLiving()
                .map(living -> CoreUtils.dataApplyPotionEffect(living, effect))
                .map(DataTransactionResult::isSuccessful)
                .orElse(false);
    }

    @Override
    protected boolean remove(ApplyableCarrier<?> character) {
        return character.getLiving()
                .map(living -> CoreUtils.dataRemovePotionEffect(living, effect))
                .map(DataTransactionResult::isSuccessful)
                .orElse(false);
    }
}
