package dev.haedhutner.skills.command.skill;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.core.command.ParentCommand;
import dev.haedhutner.core.command.annotation.Aliases;
import dev.haedhutner.core.command.annotation.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import java.util.Set;

@Aliases("skill")
@Permission("hunterskills.skill.base")
@Singleton
public class SkillCommand implements ParentCommand {

    @Inject
    CastSkillCommand castSkillCommand;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.empty();
    }

    @Override
    public Set<CommandExecutor> getChildren() {
        return Sets.newHashSet(castSkillCommand);
    }
}
