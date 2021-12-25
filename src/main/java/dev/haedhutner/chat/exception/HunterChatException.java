package dev.haedhutner.chat.exception;

import dev.haedhutner.chat.facade.ChatMessagingFacade;
import org.spongepowered.api.command.CommandException;

public class HunterChatException extends CommandException {
    public HunterChatException(Object... message) {
        super(new ChatMessagingFacade().formatError(message));
    }
}
