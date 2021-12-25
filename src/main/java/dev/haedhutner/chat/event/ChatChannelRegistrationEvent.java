package dev.haedhutner.chat.event;

import dev.haedhutner.chat.model.ChatChannel;
import dev.haedhutner.chat.service.ChatService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

public class ChatChannelRegistrationEvent implements Event {

    private Cause cause;

    private ChatService chatService;

    public ChatChannelRegistrationEvent(ChatService chatService) {
        this.chatService = chatService;
        this.cause = Cause.builder().append(chatService).build(Sponge.getCauseStackManager().getCurrentContext());
    }

    public void registerChatChannel(ChatChannel chatChannel) {
        chatService.registerChannel(chatChannel);
    }

    public ChatService getChatService() {
        return chatService;
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
