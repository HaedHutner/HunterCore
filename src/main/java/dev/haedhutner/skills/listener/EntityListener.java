package dev.haedhutner.skills.listener;

import com.google.inject.Singleton;
import dev.haedhutner.skills.facade.EffectFacade;
import dev.haedhutner.skills.facade.ResourceFacade;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import javax.inject.Inject;

@Singleton
public class EntityListener {
    @Inject
    EffectFacade effectFacade;

    @Inject
    ResourceFacade resourceFacade;

    @Listener
    public void onEntityDeath(DestructEntityEvent event, @Getter("getTargetEntity") Living entity) {
        effectFacade.onEntityDeath(entity);
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        resourceFacade.onPlayerJoin(event.getTargetEntity());
    }
}
