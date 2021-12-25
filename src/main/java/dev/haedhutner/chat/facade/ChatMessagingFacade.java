package dev.haedhutner.chat.facade;

import com.atherys.core.utils.AbstractMessagingFacade;
import com.google.inject.Singleton;

@Singleton
public class ChatMessagingFacade extends AbstractMessagingFacade {
    public ChatMessagingFacade() {
        super("Chat");
    }
}
