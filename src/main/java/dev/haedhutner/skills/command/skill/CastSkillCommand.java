package dev.haedhutner.skills.command.skill;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import dev.haedhutner.core.command.ParameterizedCommand;
import dev.haedhutner.core.command.PlayerCommand;
import dev.haedhutner.core.command.annotation.Aliases;
import dev.haedhutner.core.command.annotation.Description;
import dev.haedhutner.core.command.annotation.Permission;
import dev.haedhutner.skills.api.skill.Castable;
import dev.haedhutner.skills.command.SkillCommandElement;
import dev.haedhutner.skills.facade.SkillFacade;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@Aliases("cast")
@Permission("hunterskills.skill.cast")
@Description("Casts a given skill. You must have permission to use the skill.")
@Singleton
public class CastSkillCommand implements PlayerCommand, ParameterizedCommand {

    @Inject
    SkillFacade skillFacade;

    @Inject
    Injector injector;

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        Castable skill = args.<Castable>getOne("skill-name").get();
        String[] arguments = args.<String>getOne("arguments...").orElse("").split(" ");
        skillFacade.playerCastSkill(source, skill, arguments);
        return CommandResult.success();
    }

    @Override
    public CommandElement[] getArguments() {
        SkillCommandElement skillCommandElement = new SkillCommandElement(Text.of("skill-name"));
        injector.injectMembers(skillCommandElement);
        return new CommandElement[]{
                skillCommandElement,
                GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("arguments...")))
        };
    }
}
