package fun.stgoder.jsmpeg_relay.ps.model;

import fun.stgoder.jsmpeg_relay.ps.pusher.Pusher;

public class PusherB {
    private String streamId;
    private String source;
    private String s;
    private boolean keepAlive;
    private long cancelAfterSeconds;
    private long birthTime;
    private long upTime;

    public static PusherB fromPusher(Pusher pusher) {
        PusherB pusherB = new PusherB();
        pusherB.setStreamId(pusher.streamId());
        pusherB.setSource(pusher.source());
        pusherB.setS(pusher.s());
        pusherB.setKeepAlive(pusher.keepAlive());
        pusherB.setCancelAfterSeconds(pusher.cancelAfterSeconds());
        pusherB.setBirthTime(pusher.birthTime());
        pusherB.setUpTime(pusher.upTime());
        return pusherB;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public long getCancelAfterSeconds() {
        return cancelAfterSeconds;
    }

    public void setCancelAfterSeconds(long cancelAfterSeconds) {
        this.cancelAfterSeconds = cancelAfterSeconds;
    }

    public long getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(long birthTime) {
        this.birthTime = birthTime;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }
}
