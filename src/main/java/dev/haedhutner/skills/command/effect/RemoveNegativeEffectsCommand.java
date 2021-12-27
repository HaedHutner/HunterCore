package dev.haedhutner.skills.command.effect;

import com.google.inject.Singleton;
import dev.haedhutner.core.command.PlayerCommand;
import dev.haedhutner.core.command.annotation.Aliases;
import dev.haedhutner.core.command.annotation.Permission;
import dev.haedhutner.skills.service.EffectService;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Aliases("removenegative")
@Permission("hunterskills.effect.negative")
@Singleton
public class RemoveNegativeEffectsCommand implements PlayerCommand {

    @Inject
    EffectService effectService;

    @Override
    @Nonnull
    public CommandResult execute(@Nonnull Player src, @Nonnull CommandContext args) {
        effectService.clearNegativeEffects(src);
        return CommandResult.success();
    }
}
