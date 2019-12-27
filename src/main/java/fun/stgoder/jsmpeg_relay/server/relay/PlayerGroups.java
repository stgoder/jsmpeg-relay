package fun.stgoder.jsmpeg_relay.server.relay;

import fun.stgoder.jsmpeg_relay.server.model.StreamB;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerGroups {
    private static Map<String, PlayerGroup> playerGroupMap;

    static {
        playerGroupMap = new HashMap<>();
    }

    public static void addPlayer(String streamId, Channel channel) {
        PlayerGroup playerGroup = playerGroupMap.get(streamId);
        if (playerGroup == null)
            playerGroup = new PlayerGroup(streamId);
        playerGroupMap.put(streamId, playerGroup);
        playerGroup.addChannel(channel);
    }

    public static void removePlayer(String streamId, Channel channel) {
        PlayerGroup playerGroup = playerGroupMap.get(streamId);
        if (playerGroup == null)
            return;
        playerGroup.removeChannel(channel);
    }

    public static void broadcast(String streamId, ByteBuf message) {
        PlayerGroup playerGroup = playerGroupMap.get(streamId);
        if (playerGroup == null)
            return;
        playerGroup.broadcast(message);
    }

    public static void broadcast(ByteBuf message) {
        for (PlayerGroup playerGroup : playerGroupMap.values()) {
            playerGroup.broadcast(message);
        }
    }

    public static void destory(String streamId) {
        PlayerGroup playerGroup = playerGroupMap.get(streamId);
        if (playerGroup == null)
            return;
        playerGroup.destory();
    }

    public static void closeAndRemove(String channelId) {
        playerGroupMap.forEach((streamId, playerGroup) -> {
            if (playerGroup != null)
                playerGroup.closeAndRemove(channelId);
        });
    }

    public static List<StreamB> list() {
        List<StreamB> streams = new ArrayList<>();
        playerGroupMap.forEach((streamId, playerGroup) -> {
            StreamB streamB = playerGroup.toStreamB();
            streams.add(streamB);
        });
        return streams;
    }
}
