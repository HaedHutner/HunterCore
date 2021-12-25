package dev.haedhutner.chat.command;

import com.google.inject.Inject;
import dev.haedhutner.chat.facade.ChannelFacade;
import dev.haedhutner.chat.model.ChatChannel;
import dev.haedhutner.chat.service.ChatService;
import dev.haedhutner.core.command.PlayerCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class ChannelAliasCommand implements PlayerCommand, CommandExecutor {

    @Inject
    ChannelFacade channelFacade;

    private ChatChannel channel;

    public ChannelAliasCommand(ChatChannel channel) {
        this.channel = channel;
    }

    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .description(Text.of("Switch to/send a message to the",  TextSerializers.FORMATTING_CODE.deserialize(this.channel.getName()), " channel."))
                .executor(this)
                .arguments(this.getArguments())
                .build();
    }

    public CommandResult execute(Player player, CommandContext args) throws CommandException {
        if (args.getOne("message").isPresent()) {
            channelFacade.speakToChannel(player, channel, args.<String>getOne("message").get());
        } else {
            channelFacade.joinChannel(player, channel);
        }
        return CommandResult.success();
    }

    public CommandElement[] getArguments() {
        return new CommandElement[]{
                GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("message")))
        };
    }
}
