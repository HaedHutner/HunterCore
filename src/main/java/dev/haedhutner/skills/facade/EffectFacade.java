package dev.haedhutner.skills.facade;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.skills.api.effect.Applyable;
import dev.haedhutner.skills.service.EffectService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.Text;

import java.util.Optional;

@Singleton
public class EffectFacade {

    @Inject
    EffectService effectService;

    EffectFacade() {
    }

    public void applyEffect(Living target, String effectId) throws CommandException {
        Optional<Applyable> namedEffect = effectService.getNamedEffect(effectId);

        if (!namedEffect.isPresent()) {
            throw new CommandException(Text.of("No effect with an id of \"", effectId, "\" could be found."));
        }

        namedEffect.ifPresent(effect -> effectService.applyEffect(target, effect));
    }

    public void removeEffect(Living target, String effectId) throws CommandException {
        effectService.removeEffect(target, effectId);
    }

    public void onEntityDeath(Living living) {
        effectService.clearEffects(living);
    }

}
