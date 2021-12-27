package dev.haedhutner.core.event;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

import javax.persistence.EntityManagerFactory;

public class HunterHibernateInitializedEvent implements Event {

    private final Cause cause;

    private final EntityManagerFactory entityManagerFactory;

    public HunterHibernateInitializedEvent(EntityManagerFactory entityManagerFactory) {
        this.cause = Cause.builder().append(entityManagerFactory).build(Sponge.getCauseStackManager().getCurrentContext());
        this.entityManagerFactory = entityManagerFactory;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

}
