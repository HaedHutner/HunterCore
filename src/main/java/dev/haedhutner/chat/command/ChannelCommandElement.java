package dev.haedhutner.chat.command;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import dev.haedhutner.chat.facade.ChannelFacade;
import dev.haedhutner.chat.model.ChatChannel;
import dev.haedhutner.chat.service.ChatService;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ChannelCommandElement extends CommandElement {
    private static TextTemplate exception = TextTemplate.of("No Channel called ", TextTemplate.arg("channel"), " found.");

    @Inject
    private ChatService chatService;

    @Inject
    ChannelFacade channelFacade;

    private boolean returnMemberChannels = true;
    private boolean returnNonMemberChannels = true;

    public ChannelCommandElement(@Nullable Text key) {
        super(key);
    }

    public ChannelCommandElement(@Nullable Text key, boolean returnMemberChannels, boolean returnNonMemberChannels) {
        super(key);
        this.returnMemberChannels = returnMemberChannels;
        this.returnNonMemberChannels = returnNonMemberChannels;
    }

    @Nullable
    @Override
    protected ChatChannel parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String channel = args.next();
        if (channel.isEmpty()) {
            throw exception(channel, args);
        }
        return chatService.getChannelById(channel.toLowerCase())
                .orElseThrow(() -> exception(channel, args));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        if (!(src instanceof Player)) {
            return Collections.emptyList();
        }

        // Lists all channels that a player
        if (this.returnMemberChannels && this.returnNonMemberChannels) {
            return channelFacade.getPlayerVisibleChannels((Player) src).stream()
                    .map(ChatChannel::getId)
                    .collect(Collectors.toList());
        // List all channels that the player
        } else if (this.returnNonMemberChannels) {
            return channelFacade.getPlayerNonMemberChannels((Player) src).stream()
                    .map(ChatChannel::getId)
                    .collect(Collectors.toList());
        // List only joined channels
        } else if (this.returnMemberChannels) {
            return channelFacade.getPlayerMemberChannels((Player) src).stream()
                    .map(ChatChannel::getId)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private static ArgumentParseException exception(String channel, CommandArgs args) {
        return args.createError(exception.apply(ImmutableMap.of("channel", channel)).build());
    }
}
