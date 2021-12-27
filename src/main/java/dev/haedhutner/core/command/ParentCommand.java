package dev.haedhutner.core.command;

import org.spongepowered.api.command.spec.CommandExecutor;

import java.util.Set;

public interface ParentCommand extends CommandExecutor {

    Set<CommandExecutor> getChildren();

}
