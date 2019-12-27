package fun.stgoder.jsmpeg_relay.server.model;

import java.util.List;

public class StreamB {
    private String streamId;
    private List<ChannelB> channels;

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public List<ChannelB> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelB> channels) {
        this.channels = channels;
    }
}
