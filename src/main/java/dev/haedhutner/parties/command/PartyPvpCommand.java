package dev.haedhutner.parties.command;

import com.google.inject.Inject;
import dev.haedhutner.core.command.ParameterizedCommand;
import dev.haedhutner.core.command.PlayerCommand;
import dev.haedhutner.core.command.annotation.Aliases;
import dev.haedhutner.core.command.annotation.Description;
import dev.haedhutner.core.command.annotation.Permission;
import dev.haedhutner.parties.facade.PartyFacade;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;

@Aliases("pvp")
@Permission("hunterparties.party.pvp")
@Description("Toggles whether PvP is allowed between party members.")
public class PartyPvpCommand implements PlayerCommand, ParameterizedCommand {

    @Inject
    PartyFacade partyFacade;

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        partyFacade.setPartyPvp(source, args.<Boolean>getOne("toggle").get());

        return CommandResult.success();
    }

    @Override
    public CommandElement[] getArguments() {
        return new CommandElement[] {
                GenericArguments.bool(Text.of("toggle"))
        };
    }
}
