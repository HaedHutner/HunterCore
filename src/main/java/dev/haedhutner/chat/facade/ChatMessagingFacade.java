package dev.haedhutner.chat.facade;

import com.google.inject.Singleton;
import dev.haedhutner.core.utils.AbstractMessagingFacade;

@Singleton
public final class ChatMessagingFacade extends AbstractMessagingFacade {
    public ChatMessagingFacade() {
        super("Chat");
    }
}
