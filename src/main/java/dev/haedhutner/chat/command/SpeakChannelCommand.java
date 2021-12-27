package dev.haedhutner.chat.command;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import dev.haedhutner.chat.facade.ChannelFacade;
import dev.haedhutner.chat.model.ChatChannel;
import dev.haedhutner.core.command.ParameterizedCommand;
import dev.haedhutner.core.command.PlayerCommand;
import dev.haedhutner.core.command.annotation.Aliases;
import dev.haedhutner.core.command.annotation.Description;
import dev.haedhutner.core.command.annotation.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;

@Aliases({"speak", "say", "s"})
@Description("Speak to a channel channel")
@Permission("hunterchat.commands.speak")
@Singleton
public class SpeakChannelCommand implements PlayerCommand, ParameterizedCommand {

    @Inject
    Injector injector;

    @Inject
    ChannelFacade channelFacade;

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull Player source, @Nonnull CommandContext args) throws CommandException {
        channelFacade.speakToChannel(source, args.<ChatChannel>getOne("channel").get(),
                args.<String>getOne("message").get());
        return CommandResult.success();
    }

    @Override
    public CommandElement[] getArguments() {
        ChannelCommandElement channel = new ChannelCommandElement(Text.of("channel"));
        injector.injectMembers(channel);
        return new CommandElement[]{
                channel,
                GenericArguments.remainingJoinedStrings(Text.of("message"))
        };
    }
}
