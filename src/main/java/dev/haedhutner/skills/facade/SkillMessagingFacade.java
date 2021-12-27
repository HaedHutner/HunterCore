package dev.haedhutner.skills.facade;

import com.google.inject.Singleton;
import dev.haedhutner.core.utils.AbstractMessagingFacade;

@Singleton
public class SkillMessagingFacade extends AbstractMessagingFacade {
    public SkillMessagingFacade() {
        super("Skills");
    }
}
