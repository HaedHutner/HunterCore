package dev.haedhutner.parties;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import dev.haedhutner.chat.event.ChatChannelRegistrationEvent;
import dev.haedhutner.core.command.CommandService;
import dev.haedhutner.core.module.AbstractPluginModule;
import dev.haedhutner.core.module.ModuleResult;
import dev.haedhutner.parties.command.PartyCommand;
import dev.haedhutner.parties.data.PartyData;
import dev.haedhutner.parties.data.PartyKeys;
import dev.haedhutner.parties.facade.PartyFacade;
import dev.haedhutner.parties.listener.PlayerPartyListener;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.PluginContainer;

@Singleton
public class PartiesModule extends AbstractPluginModule {

    @Inject
    PartyFacade partyFacade;

    @Inject
    PlayerPartyListener playerPartyListener;

    @Inject
    Injector injector;

    public PartiesModule(PluginContainer container) {
        super(
                container,
                "parties",
                "Hunter Parties",
                "A module to add simple party functionality"
        );
    }

    @Override
    public ModuleResult init() {
        return ModuleResult.of(this, () -> {

            PartyKeys.PARTY_DATA_REGISTRATION = DataRegistration.builder()
                    .dataClass(PartyData.class)
                    .immutableClass(PartyData.Immutable.class)
                    .builder(new PartyData.Builder())
                    .dataName("Party")
                    .manipulatorId("party")
                    .buildAndRegister(this.getPlugin());

           return ModuleResult.success(this, "Successfully initialized");
        });
    }

    @Override
    public ModuleResult start() {
        return ModuleResult.of(this, () -> {

            Sponge.getEventManager().registerListeners(this.getPlugin(), playerPartyListener);

            try {
                new CommandService(injector).register(new PartyCommand(), this.getPlugin());
            } catch (CommandService.AnnotatedCommandException e) {
                e.printStackTrace();
            }

           return ModuleResult.success(this, "Successfully started");
        });
    }

    @Override
    public ModuleResult stop() {
        return null;
    }

    @Listener
    public void onChatChannelRegistration(ChatChannelRegistrationEvent event) {
        event.registerChatChannel(partyFacade.createPartyChatChannel(event.getChatService()));
    }
}
