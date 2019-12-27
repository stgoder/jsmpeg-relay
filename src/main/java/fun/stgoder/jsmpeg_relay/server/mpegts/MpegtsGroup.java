package fun.stgoder.jsmpeg_relay.server.mpegts;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.util.HashMap;
import java.util.Map;

public class MpegtsGroup {
    private static ChannelGroup channelGroup
            = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    private static Map<ChannelId, String> channelIdStreamIdMap = new HashMap<>();

    public static boolean contains(Channel channel) {
        return channelGroup.contains(channel) && channelIdStreamIdMap.containsKey(channel.id());
    }

    public static String getStreamId(Channel channel) {
        return channelIdStreamIdMap.get(channel.id());
    }

    public static void addChannel(String streamId, Channel channel) {
        channelGroup.add(channel);
        channelIdStreamIdMap.put(channel.id(), streamId);
    }

    public static void removeChannel(Channel channel) {
        channelGroup.remove(channel);
        channelIdStreamIdMap.remove(channel.id());
    }

    public static void broadcast(ByteBuf message) {
        if (channelGroup == null || channelGroup.isEmpty()) {
            return;
        }
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(message);
        message.retain();
        channelGroup.writeAndFlush(frame);
    }

    public static void destory() {
        if (channelGroup == null || channelGroup.isEmpty()) {
            return;
        }
        channelGroup.close();
    }
}
