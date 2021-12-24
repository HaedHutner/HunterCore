package dev.haedhutner.core.event;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

import java.util.List;

public class HunterHibernateConfigurationEvent implements Event {

    private Cause cause;

    private List<Class<?>> classes;

    public HunterHibernateConfigurationEvent(List<Class<?>> classes) {
        this.cause = Cause.builder().append(classes).build(Sponge.getCauseStackManager().getCurrentContext());
        this.classes = classes;
    }

    public void registerEntity(Class<?> clazz) {
        classes.add(clazz);
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
