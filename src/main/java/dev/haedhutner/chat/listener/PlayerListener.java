package dev.haedhutner.chat.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.chat.facade.ChannelFacade;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

@Singleton
public class PlayerListener {

    @Inject
    private ChannelFacade channelFacade;

    @Listener
    public void onJoin(ClientConnectionEvent.Join event) {
        channelFacade.onPlayerJoin(event.getTargetEntity());
    }

    @Listener
    public void onChat(MessageChannelEvent.Chat event, @Root Player player) {
        channelFacade.onPlayerChat(event, player);
    }
}
