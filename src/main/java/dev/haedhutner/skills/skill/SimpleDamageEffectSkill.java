package dev.haedhutner.skills.skill;

import dev.haedhutner.core.HunterCore;
import dev.haedhutner.skills.api.effect.ApplyableCarrier;
import dev.haedhutner.skills.api.effect.PeriodicEffect;
import dev.haedhutner.skills.api.exception.CastException;
import dev.haedhutner.skills.api.skill.CastErrors;
import dev.haedhutner.skills.api.skill.CastResult;
import dev.haedhutner.skills.api.skill.TargetedSkill;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

public class SimpleDamageEffectSkill extends TargetedSkill {

    public static class PeriodicSimpleDamageEffect extends PeriodicEffect {

        private final double damage;

        protected PeriodicSimpleDamageEffect(long timeBetweenTicks, int ticks, double damage) {
            super("periodic-simple-damage-effect", "Periodic Simple Damage Effect", timeBetweenTicks, ticks, false);
            this.damage = damage;
        }

        @Override
        protected boolean apply(ApplyableCarrier<?> character) {
            System.out.println("Damaging for " + damage);
            character.getLiving().ifPresent(living -> {
                living.damage(damage, DamageSources.GENERIC);
            });

            return true;
        }

        @Override
        protected boolean remove(ApplyableCarrier<?> character) {
            System.out.println("Removing");
            character.getLiving().ifPresent(living -> {
            });

            return true;
        }
    }

    private final double defaultDamage = 5.0;

    private final int defaultDuration = 60;

    public SimpleDamageEffectSkill() {
        super(
                "simple-damage-effect",
                "Simple Damage Skill"
        );
    }

    @Override
    public CastResult cast(Living user, Living target, long timestamp, String... args) throws CastException {
        if (args.length == 0) {
            throw CastErrors.invalidArguments();
        }

        double damage = defaultDamage;
        int ticks = defaultDuration;

        try {
            damage = Double.parseDouble(args[0]);

            if (args.length > 1) {
                ticks = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException ignored) {
        }

        if (user instanceof MessageReceiver) {
            ((MessageReceiver) user).sendMessage(Text.of("Applying effect which will deal ", damage, " damage every second for ", ticks, " ticks to ", target.getType().getName()));
        }

        PeriodicSimpleDamageEffect effect = new PeriodicSimpleDamageEffect(1000, ticks, damage);
        HunterCore.getInstance().getSkillsModule().getEffectService().applyEffect(target, effect);

        return CastResult.success();
    }

    @Override
    public long getCooldown(Living user) {
        return 5000;
    }

    @Override
    public double getResourceCost(Living user) {
        return 50.0;
    }

}
