package dev.haedhutner.chat.facade;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.chat.config.ChannelConfig;
import dev.haedhutner.chat.config.ChatConfig;
import dev.haedhutner.chat.exception.HunterChatException;
import dev.haedhutner.chat.model.ChatChannel;
import dev.haedhutner.chat.service.ChatChannelFactory;
import dev.haedhutner.chat.service.ChatService;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public final class ChannelFacade {

    @Inject
    private Logger logger;

    @Inject
    private ChatConfig chatConfig;

    @Inject
    private ChatChannelFactory chatChannelFactory;

    @Inject
    private ChatService chatService;

    @Inject
    private ChatMessagingFacade cmf;

    public ChannelFacade() {
    }

    public void registerChannels() {
        for (Map.Entry<String, ChannelConfig> entry : chatConfig.CHANNELS.entrySet()) {
            String id = entry.getKey();
            ChannelConfig channelConfig = entry.getValue();
            ChatChannel channel;

            switch (channelConfig.type) {
                case BROADCAST:
                    channel = chatChannelFactory.createBroadcastChannel(id, channelConfig);
                    break;
                case GLOBAL:
                    channel = chatChannelFactory.createGlobalChannel(id, channelConfig);
                    break;
                case WORLD:
                    channel = chatChannelFactory.createWorldChannel(id, channelConfig);
                    break;
                case RANGE:
                    channel = chatChannelFactory.createRangeChannel(id, channelConfig);
                    break;
                default:
                    logger.error("Unknown Channel type: " + channelConfig.type + " for channel" + id);
                    channel = chatChannelFactory.createGlobalChannel(id, channelConfig);
            }

            chatService.registerChannel(channel);
        }

        chatService.setDefaultChannel(chatConfig.DEFAULT_CHANNEL);
    }

    public void onPlayerJoin(Player player) {
        chatService.setDefaultChannels(player);
    }

    public void onPlayerChat(MessageChannelEvent.Chat event, Player player) {
        event.setCancelled(true);

        ChatChannel channel = chatService.getPlayerSpeakingChannel(player);
        if (!chatService.hasWritePermission(player, channel)) {
            Text message = cmf.formatError("You do not have permission to talk in the ", chatService.getChannelTextName(channel), " channel.");
            player.sendMessage(message);
            return;
        }
        channel.send(player, event.getOriginalMessage(), ChatTypes.CHAT);
    }

    public Set<ChatChannel> getPlayerVisibleChannels(Player player) {
        return chatService.getChannels().values().stream()
                .filter(channel -> channel.getPermission() == null || player.hasPermission(channel.getPermission()))
                .collect(Collectors.toSet());
    }

    public Set<ChatChannel> getPlayerMemberChannels(Player player) {
        return chatService.getChannels().values().stream()
                .filter(channel -> channel.getPlayers().contains(player.getUniqueId()))
                .collect(Collectors.toSet());
    }

    public Set<ChatChannel> getPlayerNonMemberChannels(Player player) {
        Set<ChatChannel> nonMemberChannels = new HashSet<>(getPlayerVisibleChannels(player));
        nonMemberChannels.removeAll(getPlayerMemberChannels(player));
        return nonMemberChannels;
    }

    public void joinChannel(Player source, ChatChannel channel) throws CommandException {
        if (!chatService.hasReadPermission(source, channel)) {
            throw new HunterChatException("You do not have permission to join the ", chatService.getChannelTextName(channel), " channel.");
        }
        addPlayerToChannel(source, channel);
    }

    public void leaveChannel(Player source, ChatChannel channel) throws CommandException {
        if (!chatService.hasLeavePermission(source, channel)) {
            throw new HunterChatException("You do not have permission to leave the ", chatService.getChannelTextName(channel), " channel.");
        }
        if (channel.getPlayers().contains(source.getUniqueId())) {
            removePlayerFromChannel(source, channel);
        } else {
            throw new HunterChatException("You are not in that channel.");
        }

        cmf.info(source, "You have left ", chatService.getChannelTextName(channel), ".");
    }

    public void removePlayerFromChannel(Player player, ChatChannel channel) {
        chatService.removePlayerFromChannel(player, channel);

        // If this is the players speaking channel, set it to another channel they are in
        if (channel == chatService.getPlayerSpeakingChannel(player)) {
            chatService.setPlayerSpeakingChannel(player, chatService.getPlayerChannel(player));
        }
    }

    public void addPlayerToChannel(Player player, ChatChannel channel) {
        chatService.addPlayerToChannel(player, channel);
        chatService.setPlayerSpeakingChannel(player, channel);
        cmf.info(player, "You are now chatting in ", chatService.getChannelTextName(channel), ".");
    }

    public void speakToChannel(Player player, ChatChannel channel, String message) throws CommandException {
        if (!chatService.hasWritePermission(player, channel)) {
            throw new HunterChatException("You do not have permission to talk in the ", chatService.getChannelTextName(channel), " channel.");
        }
        if (!channel.getPlayers().contains(player.getUniqueId())) {
            cmf.info(player, "You have joined ", chatService.getChannelTextName(channel), ".");
            chatService.addPlayerToChannel(player, channel);
        }
        channel.send(player, Text.of(message));
    }

    public void displayPlayerChannels(Player player) {
        Text.Builder builder;

        // List the channel they are currently speaking in
        builder = Text.builder()
                .append(Text.of(TextColors.DARK_GREEN, "Currently speaking in: "))
                .append(chatService.getChannelTextName(chatService.getPlayerSpeakingChannel(player)));
        player.sendMessage(builder.build());

        // List the currently joined in channels
        builder = Text.builder()
                .append(Text.of(TextColors.DARK_GREEN, "Joined channels: "))
                .append(Text.joinWith(Text.of(", "), getPlayerMemberChannels(player).stream()
                        .map(chatService::getChannelTextName).collect(Collectors.toSet())));
        player.sendMessage(builder.build());

        // List the available channels
        builder = Text.builder()
                .append(Text.of(TextColors.DARK_GREEN, "Available channels: "))
                .append(Text.joinWith(Text.of(", "), getPlayerNonMemberChannels(player).stream()
                        .map(chatService::getChannelTextName).collect(Collectors.toSet())));
        player.sendMessage(builder.build());
    }
}

