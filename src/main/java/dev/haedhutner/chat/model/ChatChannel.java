package dev.haedhutner.chat.model;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class ChatChannel implements MessageChannel {

    private final String id;
    private String name;
    private String permission;
    private String format;
    private String prefix;
    private String suffix;
    private Set<String> aliases;
    private Set<UUID> players;

    public ChatChannel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public String getFormat() {
        return format;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
    }

    public void setPlayers(Set<UUID> players) {
        this.players = players;
    }

    @Override
    public abstract Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type);

    @Override
    public abstract Collection<MessageReceiver> getMembers();

    public abstract Collection<MessageReceiver> getMembers(Object sender);

    @Override
    public abstract void send(@Nullable Object sender, Text original, ChatType type);
}
