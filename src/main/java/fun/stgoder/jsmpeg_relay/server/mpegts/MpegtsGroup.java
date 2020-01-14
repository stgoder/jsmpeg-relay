package fun.stgoder.jsmpeg_relay.server.mpegts;

import fun.stgoder.jsmpeg_relay.server.model.ChannelB;
import fun.stgoder.jsmpeg_relay.server.model.MpegtsStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static void closeAndRemove(String channelId) {
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

    public static void destroy() {
        if (channelGroup == null || channelGroup.isEmpty()) {
            return;
        }
        channelGroup.close();
    }

    public static List<MpegtsStream> list() {
        List<MpegtsStream> streams = new ArrayList<>();
        channelGroup.iterator().forEachRemaining(channel -> {
            String channelId = channel.id().toString();
            boolean active = channel.isActive();
            MpegtsStream stream = new MpegtsStream();
            stream.setStreamId(channelIdStreamIdMap.get(channel.id()));
            stream.setChannelId(channelId);
            stream.setActive(active);
            streams.add(stream);
        });
        return streams;
    }
}
