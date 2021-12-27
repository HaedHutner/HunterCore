package dev.haedhutner.chat.command;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.chat.facade.ChannelFacade;
import dev.haedhutner.core.command.ParentCommand;
import dev.haedhutner.core.command.PlayerCommand;
import dev.haedhutner.core.command.annotation.Aliases;
import dev.haedhutner.core.command.annotation.Description;
import dev.haedhutner.core.command.annotation.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Set;

@Aliases("chat")
@Description("Base chat command.")
@Permission("hunterchat.commands")
@Singleton
public class ChatCommand implements PlayerCommand, ParentCommand {

    @Inject
    ChannelFacade channelFacade;

    @Inject
    JoinChannelCommand joinChannelCommand;

    @Inject
    LeaveChannelCommand leaveChannelCommand;

    @Inject
    SpeakChannelCommand speakChannelCommand;

    @Override
    public CommandResult execute(Player src, CommandContext args) {
        channelFacade.displayPlayerChannels(src);
        return CommandResult.success();
    }

    @Override
    public Set<CommandExecutor> getChildren() {
        return Sets.newHashSet(
                joinChannelCommand,
                leaveChannelCommand,
                speakChannelCommand
        );
    }
}
