package dev.haedhutner.core.event;

import dev.haedhutner.core.HunterCore;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

import java.util.ArrayList;
import java.util.List;

public class HunterDatabaseMigrationEvent implements Event {

    private final Cause cause;

    private final List<String> pluginIds = new ArrayList<>();

    public HunterDatabaseMigrationEvent() {
        this.cause = Cause.builder().append(HunterCore.getInstance()).build(Sponge.getCauseStackManager().getCurrentContext());
    }

    public void registerForMigration(String pluginId) {
        this.pluginIds.add(pluginId);
    }

    public List<String> getPluginIds() {
        return pluginIds;
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
