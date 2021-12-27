package dev.haedhutner.skills.api.exception;

import dev.haedhutner.skills.facade.SkillMessagingFacade;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;

public class CastException extends CommandException {

    public CastException(Text error) {
        super(new SkillMessagingFacade().formatInfo(error));
    }
}
