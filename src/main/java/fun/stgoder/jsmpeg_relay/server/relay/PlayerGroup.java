package fun.stgoder.jsmpeg_relay.server.relay;

import fun.stgoder.jsmpeg_relay.server.model.ChannelB;
import fun.stgoder.jsmpeg_relay.server.model.PlayerStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.util.ArrayList;
import java.util.List;

public class PlayerGroup {
    private ChannelGroup channelGroup
            = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    private String streamId;

    public PlayerGroup(String streamId) {
        this.streamId = streamId;
    }

    public void addChannel(Channel channel) {
        channelGroup.add(channel);
    }

    public void removeChannel(Channel channel) {
        channelGroup.remove(channel);
    }

    public void broadcast(ByteBuf message) {
        if (channelGroup == null || channelGroup.isEmpty()) {
            return;
        }
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(message);
        message.retain();
        channelGroup.writeAndFlush(frame);
    }

    public void destroy() {
        if (channelGroup == null || channelGroup.isEmpty()) {
            return;
        }
        channelGroup.close();
    }

    public String streamId() {
        return streamId;
    }

    public void closeAndRemove(String channelId) {
        Channel[] channels = new Channel[channelGroup.size()];
        channelGroup.toArray(channels);
        for (Channel channel : channels) {
            if (channel.id().toString().equals(channelId)) {
                channel.close();
                channelGroup.remove(channel);
                break;
            }
        }
    }

    public PlayerStream toStreamB() {
        PlayerStream streamB = new PlayerStream();
        List<ChannelB> list = new ArrayList<>();
        channelGroup.iterator().forEachRemaining(channel -> {
            String channelId = channel.id().toString();
            boolean active = channel.isActive();
            ChannelB channelB = new ChannelB();
            channelB.setChannelId(channelId);
            channelB.setActive(active);
            list.add(channelB);
        });
        streamB.setStreamId(streamId);
        streamB.setChannels(list);
        return streamB;
    }
}
