package dev.haedhutner.skills.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.skills.api.event.ResourceEvent;
import dev.haedhutner.skills.facade.ResourceFacade;
import org.spongepowered.api.event.Listener;

@Singleton
public class ResourceListener {
    @Inject
    private ResourceFacade resourceFacade;

    @Listener
    public void onResourceRegen(ResourceEvent.Regen event) {
        resourceFacade.onResourceRegen(event);
    }
}
