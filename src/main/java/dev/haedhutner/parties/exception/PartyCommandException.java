package dev.haedhutner.parties.exception;

import dev.haedhutner.parties.facade.PartyMessagingFacade;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.User;

public class PartyCommandException extends CommandException {
    public PartyCommandException(Object... message) {
        super(
                new PartyMessagingFacade().formatError(message)
        );
    }

    public static PartyCommandException noParty() {
        return new PartyCommandException("You are not in a party.");
    }

    public static PartyCommandException notLeader() {
        return new PartyCommandException("You are not the party leader.");
    }

    public static PartyCommandException notInParty(User player) {
        return new PartyCommandException(player.getName(), " is not in your party!");
    }
}
