package dev.haedhutner.chat.command;

import com.google.inject.Inject;
import dev.haedhutner.chat.facade.ChannelFacade;
import dev.haedhutner.core.command.PlayerCommand;
import dev.haedhutner.core.command.annotation.Aliases;
import dev.haedhutner.core.command.annotation.Children;
import dev.haedhutner.core.command.annotation.Description;
import dev.haedhutner.core.command.annotation.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

@Aliases("chat")
@Description("Base chat command.")
@Permission("hunterchat.commands")
@Children({
        JoinChannelCommand.class,
        LeaveChannelCommand.class,
        SpeakChannelCommand.class
})
public class ChatCommand implements PlayerCommand, CommandExecutor {

    @Inject
    ChannelFacade channelFacade;

    @Override
    public CommandResult execute(Player src, CommandContext args) {
        channelFacade.displayPlayerChannels(src);
        return CommandResult.success();
    }
}
