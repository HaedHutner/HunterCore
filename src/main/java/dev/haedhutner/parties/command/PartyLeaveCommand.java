package dev.haedhutner.parties.command;

import com.google.inject.Inject;
import dev.haedhutner.core.command.PlayerCommand;
import dev.haedhutner.core.command.annotation.Aliases;
import dev.haedhutner.core.command.annotation.Description;
import dev.haedhutner.core.command.annotation.Permission;
import dev.haedhutner.parties.facade.PartyFacade;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;

@Aliases("leave")
@Permission("hunterparties.party.leave")
@Description("Leaves your party.")
public class PartyLeaveCommand implements PlayerCommand {

    @Inject
    PartyFacade partyFacade;

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        partyFacade.leaveParty(source);

        return CommandResult.success();
    }
}
