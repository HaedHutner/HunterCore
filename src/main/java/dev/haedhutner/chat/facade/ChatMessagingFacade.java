package dev.haedhutner.chat.facade;

import com.google.inject.Singleton;
import dev.haedhutner.core.utils.AbstractMessagingFacade;

@Singleton
public class ChatMessagingFacade extends AbstractMessagingFacade {
    public ChatMessagingFacade() {
        super("Chat");
    }
}
