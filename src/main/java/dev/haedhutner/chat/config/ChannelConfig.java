package dev.haedhutner.chat.config;

import dev.haedhutner.chat.model.ChannelType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashSet;
import java.util.Set;

@ConfigSerializable
public class ChannelConfig {

    @Setting("name")
    public String name = "&7Global";

    /**
     * If the configured permission is "example.plugin.someChat",
     * then the following permissions can be assigned:
     *
     * * "example.plugin.someChat.read" -- for read access
     * * "example.plugin.someChat.speak" -- for write access
     * * "example.plugin.someChat.join" -- permission to join the channel
     * * "example.plugin.someChat.leave" -- permission to leave the channel
     * * "example.plugin.someChat.format" -- for sending formatted messages
     *
     * If this is null, then by default it is understood that all players have permissions to read, write, format
     */
    @Setting("permission")
    public String permission;

    @Setting("type")
    public ChannelType type = ChannelType.GLOBAL;

    @Setting("format")
    public String format = "%cprefix %player: %message %csuffix";

    @Setting("prefix")
    public String prefix = "[§2Global&r]";

    @Setting("suffix")
    public String suffix;

    @Setting("aliases")
    public Set<String> aliases = new HashSet<>();

    @Setting("range")
    public int range;

}
