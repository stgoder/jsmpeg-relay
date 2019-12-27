package fun.stgoder.jsmpeg_relay.server.model;

public class ChannelB {
    private String channelId;
    private boolean alive;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
