package dev.haedhutner.skills.command.effect;

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

@Aliases("effect")
@Permission("hunterskills.effect.base")
@Singleton
public class EffectCommand implements ParentCommand {

    @Inject
    ApplyEffectCommand applyEffectCommand;

    @Inject
    RemoveEffectCommand removeEffectCommand;

    @Inject
    RemoveNegativeEffectsCommand removeNegativeEffectsCommand;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return CommandResult.empty();
    }

    @Override
    public Set<CommandExecutor> getChildren() {
        return Sets.newHashSet(applyEffectCommand, removeEffectCommand, removeNegativeEffectsCommand);
    }
}
