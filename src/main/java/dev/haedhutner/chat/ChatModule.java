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
import dev.haedhutner.core.module.config.ModuleConfiguration;
import dev.haedhutner.skills.SkillsConfig;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Map;
import java.util.Optional;

@Singleton
public class ChatModule extends AbstractPluginModule {

    public static final String ID = "chat";

    @Inject
    private Logger logger;

    @Inject
    private Injector injector;

    @Inject
    private ChatConfig config;

    @Inject
    private PlayerListener playerListener;

    @Inject
    private CommandService commandService;

    @Inject
    private ChatCommand chatCommand;

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

            // Register listeners
            Sponge.getEventManager().registerListeners(getPlugin(), playerListener);

            try {
                commandService.register(chatCommand, getPlugin());
            } catch (CommandService.AnnotatedCommandException e) {
                e.printStackTrace();
            }

            getChannelFacade().registerChannels();

            return ModuleResult.success(this, "Successfully Initialized");
        });
    }

    @Override
    public ModuleResult start() {
        return ModuleResult.success(this, "Successfully started");
    }

    @Override
    public ModuleResult stop() {
        return ModuleResult.success(this, "Successfully stopped");
    }

    @Override
    public Optional<ModuleConfiguration> getConfiguration() {
        return Optional.of(config);
    }

    public ChatConfig getConfig() {
        return config;
    }

    public ChannelFacade getChannelFacade() {
        return injector.getInstance(ChannelFacade.class);
    }

    public ChatMessagingFacade getChatMessagingFacade() {
        return injector.getInstance(ChatMessagingFacade.class);
    }

    public ChatChannelFactory getChatChannelFactory() {
        return injector.getInstance(ChatChannelFactory.class);
    }

    public ChatService getChatService() {
        return injector.getInstance(ChatService.class);
    }
}
