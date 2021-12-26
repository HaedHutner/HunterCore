package dev.haedhutner.chat;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import dev.haedhutner.chat.command.ChatCommand;
import dev.haedhutner.chat.config.ChannelConfig;
import dev.haedhutner.chat.config.ChatConfig;
import dev.haedhutner.chat.facade.ChannelFacade;
import dev.haedhutner.chat.facade.ChatMessagingFacade;
import dev.haedhutner.chat.listener.PlayerListener;
import dev.haedhutner.chat.model.ChatChannel;
import dev.haedhutner.chat.service.ChatChannelFactory;
import dev.haedhutner.chat.service.ChatService;
import dev.haedhutner.core.command.CommandService;
import dev.haedhutner.core.module.AbstractPluginModule;
import dev.haedhutner.core.module.ModuleResult;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Map;

@Singleton
public class ChatModule extends AbstractPluginModule {

    public static final String ID = "chat";

    @Inject
    private Logger logger;

    @Inject
    private ChatConfig chatConfig;

    @Inject
    private ChannelFacade channelFacade;

    @Inject
    private ChatMessagingFacade chatMessagingFacade;

    @Inject
    private ChatChannelFactory chatChannelFactory;

    @Inject
    private ChatService chatService;

    @Inject
    private Injector injector;

    @Inject
    private PlayerListener playerListener;

    @Inject
    private CommandService commandService;

    @Inject
    public ChatModule(PluginContainer plugin) {
        super(
                plugin,
                ID,
                "Hunter Chat",
                "A module to extend & improve chat functionality"
        );
    }

    @Override
    public ModuleResult init() {
        return ModuleResult.of(this, () -> {
            // is this necessary? is the chat service used anywhere from the service manager?
            //Sponge.getServiceManager().setProvider(container, ChatService.class, components.chatService);

            chatConfig.init(getPlugin(), this);

            // Register listeners
            Sponge.getEventManager().registerListeners(getPlugin(), playerListener);

            try {
                commandService.register(new ChatCommand(), getPlugin());
            } catch (CommandService.AnnotatedCommandException e) {
                e.printStackTrace();
            }

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

            return ModuleResult.success(this, "Successfully Initialized");
        });
    }

    @Override
    public ModuleResult start() {
        return ModuleResult.of(this, () -> {
            setStarted(true);
            return ModuleResult.success(this, "Successfully started");
        });
    }

    @Override
    public ModuleResult stop() {
        return ModuleResult.success(this, "Successfully stopped");
    }

    public ChannelFacade getChannelFacade() {
        return channelFacade;
    }

    public ChatMessagingFacade getChatMessagingFacade() {
        return chatMessagingFacade;
    }

    public ChatChannelFactory getChatChannelFactory() {
        return chatChannelFactory;
    }

    public ChatService getChatService() {
        return chatService;
    }
}
