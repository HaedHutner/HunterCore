package dev.haedhutner.parties.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.chat.event.ChatChannelRegistrationEvent;
import dev.haedhutner.parties.facade.PartyFacade;
import dev.haedhutner.parties.service.PartyService;
import org.spongepowered.api.event.Listener;

@Singleton
public class ChatChannelRegistrationListener {

    @Inject
    PartyFacade partyFacade;

    @Listener
    public void onChatChannelRegistration(ChatChannelRegistrationEvent event) {
        event.registerChatChannel(partyFacade.createPartyChatChannel(event.getChatService()));
    }

}
