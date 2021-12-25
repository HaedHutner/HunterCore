package dev.haedhutner.chat.service;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import dev.haedhutner.chat.command.ChannelAliasCommand;
import dev.haedhutner.chat.config.ChannelConfig;
import dev.haedhutner.chat.config.ChatConfig;
import dev.haedhutner.chat.model.*;
import dev.haedhutner.core.HunterCore;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.ChatTypeMessageReceiver;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class ChatService {
    private static final String READ_POSTFIX = ".read";
    private static final String WRITE_POSTFIX = ".write";
    private static final String LEAVE_POSTFIX = ".toggle";
    private static final String FORMAT_POSTFIX = ".format";

    @Inject
    private Injector injector;

    @Inject
    private ChatConfig config;

    private final Map<String, ChatChannel> channels = new HashMap<>();

    private final Map<UUID, ChatChannel> playerSpeakingMap = new HashMap<>();

    private final Set<ChatChannel> autoJoinChannels = new HashSet<>();

    private ChatChannel defaultChannel;

    public void setDefaultChannel(String channelId) {
        this.defaultChannel = channels.get(channelId);
    }

    public void registerChannel(ChatChannel channel) {
        this.channels.put(channel.getId(), channel);

        // Register aliases
        if (!channel.getAliases().isEmpty()) {
            String[] aliases = new String[channel.getAliases().size()];
            channel.getAliases().toArray(aliases);

            ChannelAliasCommand channelAliasCommand = new ChannelAliasCommand(channel);
            injector.injectMembers(channelAliasCommand);

            Sponge.getCommandManager().register(HunterCore.getInstance(), channelAliasCommand.getSpec(), aliases);
        }

        if (config.AUTO_JOIN_CHANNELS.contains(channel.getId())) {
            this.autoJoinChannels.add(channel);
        }
    }

    public Map<String, ChatChannel> getChannels() {
        return channels;
    }

    public void setDefaultChannels(Player player) {
        for (ChatChannel channel : autoJoinChannels) {
            addPlayerToChannel(player, channel);
        }
        addPlayerToChannel(player, defaultChannel);
        setPlayerSpeakingChannel(player, defaultChannel);
    }

    public ChatChannel getPlayerSpeakingChannel(Player player) {
        return playerSpeakingMap.getOrDefault(player.getUniqueId(), defaultChannel);
    }

    public void setPlayerSpeakingChannel(Player player, ChatChannel channel) {
        playerSpeakingMap.put(player.getUniqueId(), channel);
    }

    public ChatChannel getPlayerChannel(Player player) {
        for (ChatChannel channel : channels.values()) {
            if (channel.getPlayers().contains(player.getUniqueId())) {
                return channel;
            }
        }
        return defaultChannel;
    }

    public Optional<ChatChannel> getChannelById(String id) {
        return Optional.ofNullable(channels.get(id));
    }

    public void addPlayerToChannel(Player player, ChatChannel channel) {
        channel.getPlayers().add(player.getUniqueId());
    }

    public void removePlayerFromChannel(Player player, ChatChannel channel) {
        channel.getPlayers().remove(player.getUniqueId());
    }

    public boolean hasReadPermission(CommandSource src, ChatChannel channel) {
        return src.hasPermission(channel.getPermission() + READ_POSTFIX);
    }

    public boolean hasWritePermission(CommandSource src, ChatChannel channel) {
        return src.hasPermission(channel.getPermission() + WRITE_POSTFIX);
    }

    public boolean hasLeavePermission(CommandSource src, ChatChannel channel) {
        return src.hasPermission(channel.getPermission() + LEAVE_POSTFIX);
    }

    public boolean hasFormatPermission(CommandSource src, ChatChannel channel) {
        return src.hasPermission(channel.getPermission() + FORMAT_POSTFIX);
    }

    public Text getChannelTextName(ChatChannel channel) {
        return TextSerializers.FORMATTING_CODE.deserialize(channel.getName());
    }

    public ChatChannel populateChatChannel(ChatChannel chatChannel, ChannelConfig channelConfig) {
        chatChannel.setPermission(channelConfig.permission);
        chatChannel.setFormat(channelConfig.format);
        chatChannel.setName(channelConfig.name);
        chatChannel.setPrefix(channelConfig.prefix);
        chatChannel.setSuffix(channelConfig.suffix);
        chatChannel.setAliases(channelConfig.aliases);

        return chatChannel;
    }

    public Set<MessageReceiver> getChannelMembers(ChatChannel chatChannel) {
        return Sponge.getServer().getOnlinePlayers().stream()
                .filter(player -> chatChannel.getPlayers().contains(player.getUniqueId()))
                .collect(Collectors.toSet());
    }

    public void sendMessageToChannel(ChatChannel chatChannel, @Nullable Object sender, Text original, ChatType type) {
        checkNotNull(original, "original text");
        checkNotNull(type, "type");

        for (MessageReceiver member : chatChannel.getMembers(sender)) {
            if (member instanceof Player && !hasReadPermission((Player) member, chatChannel)) {
                // Allow a user to read their own messages
                if (sender == null || !sender.equals(member)) {
                    continue;
                }
            }

            if (member instanceof ChatTypeMessageReceiver) {
                formatMessage(chatChannel, sender, member, original).ifPresent(text -> ((ChatTypeMessageReceiver) member).sendMessage(type, text));
            } else {
                formatMessage(chatChannel, sender, member, original).ifPresent(member::sendMessage);
            }
        }
    }

    public Optional<Text> formatMessage(ChatChannel channel, Object sender, MessageReceiver receiver, Text original) {
        String playerName = "";
        String world = "";
        String prefix = "";
        String suffix = "";
        String message = original.toPlain();

        // Remove the player information from the message
        if (sender instanceof CommandSource) {
            CommandSource commandSource = (CommandSource) sender;
            playerName = commandSource.getName();
            prefix = commandSource.getOption("prefix").orElse("");
            suffix = commandSource.getOption("suffix").orElse("");
            message = message.replaceFirst("<" + playerName + ">", "").trim();

            if (!hasFormatPermission(commandSource, channel)) {
                message = TextSerializers.FORMATTING_CODE.stripCodes(message);
            }
        }

        if (sender instanceof Player) {
            world = ((Player) sender).getWorld().getName();
        }

        // Replace variables
        String format = channel.getFormat()
                .replace("%prefix", prefix)
                .replace("%suffix", suffix)
                .replace("%cprefix", Optional.ofNullable(channel.getPrefix()).orElse(""))
                .replace("%csuffix", Optional.ofNullable(channel.getSuffix()).orElse(""))
                .replace("%player", playerName)
                .replace("%message", message)
                .replace("%world", world);

        Text.Builder builder = Text.builder();
        builder.append(TextSerializers.FORMATTING_CODE.deserialize(format));
        return Optional.of(builder.build());
    }

    public Collection<MessageReceiver> getRangeChannelReceivers(Object sender, ChatChannel chatChannel, int range) {
        if (sender instanceof Player && range > 0) {
            Player player = (Player) sender;

            return player.getNearbyEntities(range).stream()
                    .filter(entity -> entity instanceof Player && chatChannel.getPlayers().contains(entity.getUniqueId()))
                    .map(entity -> (Player) entity)
                    .collect(Collectors.toSet());
        }

        return chatChannel.getMembers();
    }

    public Collection<MessageReceiver> getGlobalChannelReceivers(Object sender, ChatChannel chatChannel) {
        return Sponge.getServer().getOnlinePlayers().stream()
                .filter(player -> chatChannel.getPlayers().contains(player.getUniqueId()))
                .collect(Collectors.toSet());
    }

    public Collection<MessageReceiver> getWorldChannelReceivers(Object sender, ChatChannel chatChannel) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            return player.getWorld().getPlayers().stream()
                    .filter(onlinePlayer -> chatChannel.getPlayers().contains(onlinePlayer.getUniqueId()))
                    .collect(Collectors.toSet());
        }

        return chatChannel.getMembers();
    }

    public Collection<MessageReceiver> getBroadcastChannelReceivers(Object sender, ChatChannel chatChannel) {
        return new HashSet<>(Sponge.getServer().getOnlinePlayers());
    }
}
