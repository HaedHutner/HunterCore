package dev.haedhutner.chat.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.haedhutner.chat.config.ChannelConfig;
import dev.haedhutner.chat.model.ChatChannel;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

@Singleton
public final class ChatChannelFactory {

    @Inject
    ChatService chatService;

    public ChatChannel createRangeChannel(String id, ChannelConfig channelConfig) {
        InternalChatChannel chatChannel = new InternalChatChannel(id) {
            @Override
            public Collection<MessageReceiver> getMembers(Object sender) {
                return chatService.getRangeChannelReceivers(sender, this, channelConfig.range);
            }
        };

        return chatService.populateChatChannel(chatChannel, channelConfig);
    }

    public ChatChannel createGlobalChannel(String id, ChannelConfig channelConfig) {
        InternalChatChannel chatChannel = new InternalChatChannel(id) {
            @Override
            public Collection<MessageReceiver> getMembers(Object sender) {
                return chatService.getGlobalChannelReceivers(sender, this);
            }
        };

        return chatService.populateChatChannel(chatChannel, channelConfig);
    }

    public ChatChannel createWorldChannel(String id, ChannelConfig channelConfig) {
        InternalChatChannel chatChannel = new InternalChatChannel(id) {
            @Override
            public Collection<MessageReceiver> getMembers(Object sender) {
                return chatService.getWorldChannelReceivers(sender, this);
            }
        };

        return chatService.populateChatChannel(chatChannel, channelConfig);
    }

    public ChatChannel createBroadcastChannel(String id, ChannelConfig channelConfig) {
        InternalChatChannel chatChannel = new InternalChatChannel(id) {
            @Override
            public Collection<MessageReceiver> getMembers(Object sender) {
                return chatService.getBroadcastChannelReceivers(sender, this);
            }
        };

        return chatService.populateChatChannel(chatChannel, channelConfig);
    }

    private abstract class InternalChatChannel extends ChatChannel {
        public InternalChatChannel(String id) {
            super(id);
        }

        @Override
        public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {
            return chatService.formatMessage(this, sender, recipient, original);
        }

        @Override
        public Collection<MessageReceiver> getMembers() {
            return chatService.getChannelMembers(this);
        }

        @Override
        public void send(@Nullable Object sender, Text original, ChatType type) {
            chatService.sendMessageToChannel(this, sender, original, type);
        }
    }

}
