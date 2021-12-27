package dev.haedhutner.chat.config;

import com.google.inject.Inject;
import dev.haedhutner.chat.ChatModule;
import dev.haedhutner.chat.model.ChannelType;
import dev.haedhutner.core.module.config.ModuleConfiguration;
import ninja.leaping.configurate.objectmapping.Setting;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChatConfig extends ModuleConfiguration {

    @Setting("channels")
    public Map<String, ChannelConfig> CHANNELS = new HashMap<>();
    {
        // Setup some default channels
        CHANNELS.put("global", new ChannelConfig());

        ChannelConfig local = new ChannelConfig();
        local.name = "&3Local";
        local.type = ChannelType.RANGE;
        local.prefix = "[&3Local&r]";
        local.range = 20;
        CHANNELS.put("local", local);

        ChannelConfig broadcast = new ChannelConfig();
        broadcast.name = "&4Broadcast";
        broadcast.type = ChannelType.BROADCAST;
        broadcast.prefix = "[&4Broadcast&r]";
        CHANNELS.put("broadcast", broadcast);

        ChannelConfig world = new ChannelConfig();
        world.name = "&5World";
        world.type = ChannelType.WORLD;
        world.prefix = "[&5World&r]";
        CHANNELS.put("world", world);
    }

    @Setting(value = "default-channel", comment = "The default channel players will speak to")
    public String DEFAULT_CHANNEL = "global";

    @Setting(value = "auto-join-channels", comment = "List of channels to join upon login")
    public Set<String> AUTO_JOIN_CHANNELS = new HashSet<>();
    {
        AUTO_JOIN_CHANNELS.add("local");
    }

    @Inject
    protected ChatConfig(PluginContainer plugin) {
        super(plugin, ChatModule.ID);
    }
}
