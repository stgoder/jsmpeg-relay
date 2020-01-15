package fun.stgoder.jsmpeg_relay.common.db;

import fun.stgoder.jsmpeg_relay.common.db.annotation.col;
import fun.stgoder.jsmpeg_relay.common.db.annotation.tbl;

@tbl("pusher")
public class PusherEntity {
    public static final String BCOLS = "stream_id, source, keep_alive, cancel_after_seconds, birth_time, up_time";
    public static final String COLS = "stream_id as streamId, " +
            "source, " +
            "keep_alive as keepAlive, " +
            "cancel_after_seconds as cancelAfterSeconds, " +
            "birth_time as birthTime, " +
            "up_time as upTime";
    public static final String VALUES = ":stream_id, :source, :keep_alive, " +
            ":cancel_after_seconds, :birth_time, :up_time";
    @col(value = "stream_id", pk = true, nn = true)
    private String streamId;
    @col(nn = true, len = 255)
    private String source;
    @col(value = "keep_alive", nn = true)
    private boolean keepAlive;
    @col(value = "cancel_after_seconds", nn = true)
    private long cancelAfterSeconds;
    @col(value = "birth_time", nn = true)
    private long birthTime;
    @col(value = "up_time", nn = true)
    private long upTime;

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
