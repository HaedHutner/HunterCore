package dev.haedhutner.skills.command.effect;

import com.google.inject.Singleton;
import dev.haedhutner.core.command.ParameterizedCommand;
import dev.haedhutner.core.command.annotation.Aliases;
import dev.haedhutner.core.command.annotation.Permission;
import dev.haedhutner.skills.facade.EffectFacade;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Aliases("remove")
@Permission("hunterskills.effect.remove")
@Singleton
public class RemoveEffectCommand implements ParameterizedCommand {

    @Inject
    EffectFacade effectFacade;

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource source, @Nonnull CommandContext args) throws CommandException {
        Player player = args.<Player>getOne("player").orElse(null);
        String effect = args.<String>getOne("effect-id").orElse(null);
        effectFacade.removeEffect(player, effect);
        return CommandResult.success();
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.player(Text.of("player")),
                GenericArguments.string(Text.of("effect-id"))
        };
    }
}
