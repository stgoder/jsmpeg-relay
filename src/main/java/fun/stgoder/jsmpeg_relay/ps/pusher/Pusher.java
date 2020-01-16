package fun.stgoder.jsmpeg_relay.ps.pusher;

import fun.stgoder.jsmpeg_relay.common.Constants;
import fun.stgoder.jsmpeg_relay.common.OS;
import fun.stgoder.jsmpeg_relay.common.db.Ds;
import fun.stgoder.jsmpeg_relay.common.db.Param;
import fun.stgoder.jsmpeg_relay.common.db.PusherEntity;
import fun.stgoder.jsmpeg_relay.common.db.Sql;
import fun.stgoder.jsmpeg_relay.common.exception.ExecException;
import fun.stgoder.jsmpeg_relay.ps.Cmd;
import fun.stgoder.jsmpeg_relay.ps.Ps;
import fun.stgoder.jsmpeg_relay.server.relay.PlayerGroups;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pusher {
    private static final Map<String, Pusher> pushers;
    private static final StatusChecker statusChecker;

    static {
        pushers = new HashMap<>();
        statusChecker = new StatusChecker();
        statusChecker.start();
    }

    public static void startAndPut(String streamId,
                                   String source,
                                   boolean keepAlive,
                                   long cancelAfterSeconds) throws ExecException {
        if (pushers.containsKey(streamId))
            return;
        long now = System.currentTimeMillis();
        Pusher pusher = new Pusher(streamId, source)
                .keepAlive(keepAlive)
                .cancelAfterSeconds(cancelAfterSeconds)
                .pushToJsmpegRelay()
                .birthTime(now)
                .upTime(now);
        pushers.put(streamId, pusher);
        Ds.sqlite0.insert(
                new Sql()
                        .insert(PusherEntity.class)
                        .cols(PusherEntity.BCOLS)
                        .values(PusherEntity.VALUES).sql(),
                new Param()
                        .add("stream_id", streamId)
                        .add("source", source)
                        .add("keep_alive", keepAlive)
                        .add("cancel_after_seconds", cancelAfterSeconds)
                        .add("birth_time", now)
                        .add("up_time", now));
    }

    public static void stopAndRemove(String streamId) {
        Pusher pusher = pushers.get(streamId);
        if (pusher == null)
            return;
        pusher.cleanup();
        pushers.remove(streamId);
        PlayerGroups.removeGroup(streamId);
        Ds.sqlite0.delete(
                new Sql()
                        .delete(PusherEntity.class)
                        .where("stream_id = :stream_id").sql(),
                new Param("stream_id", streamId));
    }

    public static void loadFromDB() {
        new Thread(() -> {
            List<PusherEntity> pusherEntities = Ds.sqlite0.select(
                    new Sql()
                            .select(PusherEntity.COLS)
                            .from(PusherEntity.class).sql(), PusherEntity.class);
            for (PusherEntity pusherEntity : pusherEntities) {
                long now = System.currentTimeMillis();
                String streamId = pusherEntity.getStreamId();
                Pusher pusher = new Pusher(streamId, pusherEntity.getSource())
                        .keepAlive(pusherEntity.isKeepAlive())
                        .cancelAfterSeconds(pusherEntity.getCancelAfterSeconds())
                        .birthTime(pusherEntity.getBirthTime())
                        .upTime(now);
                try {
                    pusher.pushToJsmpegRelay();
                    pushers.put(streamId, pusher);
                    Ds.sqlite0.update(
                            new Sql()
                                    .update(PusherEntity.class)
                                    .set("up_time = :up_time")
                                    .where("stream_id = :stream_id").sql(),
                            new Param()
                                    .add("up_time", now)
                                    .add("stream_id", streamId));
                } catch (ExecException e) {
                    e.printStackTrace();
                    System.err.println("load pusher: " + streamId + " err");
                }
            }
        }).start();
    }

    public static Collection<Pusher> pushers() {
        return pushers.values();
    }

    private String streamId;
    private String source;
    private Ps ps;
    private boolean keepAlive;
    private long cancelAfterSeconds;
    private long birthTime;
    private long upTime;

    public Pusher(String streamId, String source) {
        this.streamId = streamId;
        this.source = source;
        boolean isFile = false;
        try {
            File file = new File(source);
            if (file.isFile() && file.exists())
                isFile = true;
        } catch (Exception e) {
            System.out.println("source not file");
        }
        Cmd cmd = new Cmd();
        if (OS.isLINUX() || OS.isMAC()) {
            cmd.add(Constants.FFMPEG_PATH);
            if (!isFile) {
                cmd.add("-rtsp_transport")
                        .add("tcp")
                        .add("-stimeout")
                        .add("5000000");
            }
            cmd.add("-re")
                    .add("-i")
                    .add(source)
                    .add("-f")
                    .add("mpegts")
                    .add("-codec:v")
                    .add("mpeg1video")
                    .add("-nostats")
                    .add("-r")
                    .add("25")
                    .add("-b:v")
                    .add("1000k")
                    .add("-s")
                    .add("640x480");
            if (!isFile) {
                cmd.add("-stimeout")
                        .add("5000000"); // keep alive
            }
            cmd.add("http://127.0.0.1:" + Constants.MPEGTS_SERVER_PORT + "/" + streamId)
                    .add("-loglevel")
                    .add("error");
        }
        if (OS.isWIN()) {
            cmd.add(Constants.FFMPEG_PATH);
            if (!isFile) {
                cmd.add("-rtsp_transport")
                        .add("tcp")
                        .add("-stimeout")
                        .add("5000000");
            }
            cmd.add("-re")
                    .add("-i")
                    .add(source)
                    .add("-f")
                    .add("mpegts")
                    .add("-codec:v")
                    .add("mpeg1video")
                    .add("-nostats")
                    .add("-r")
                    .add("25")
                    .add("-b:v")
                    .add("1000k")
                    .add("-s")
                    .add("640x480")
                    .add("-bf")
                    .add("0");
            if (!isFile) {
                cmd.add("-stimeout")
                        .add("5000000"); // keep alive
            }
            cmd.add("http://127.0.0.1:" + Constants.MPEGTS_SERVER_PORT + "/" + streamId)
                    .add("-loglevel")
                    .add("error");
        }
        this.ps = new Ps(cmd);
    }

    public Pusher pushToJsmpegRelay() throws ExecException {
        ps.execRedirect(new File(Constants.PSLOG_PATH + File.separator + streamId + ".log"));
        return this;
    }

    public void cleanup() {
        ps.cleanup();
    }

    public boolean isAlive() {
        return ps.isAlive();
    }

    public String streamId() {
        return streamId;
    }

    public String source() {
        return source;
    }

    public Ps ps() {
        return ps;
    }

    public boolean keepAlive() {
        return keepAlive;
    }

    public Pusher keepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    public long cancelAfterSeconds() {
        return cancelAfterSeconds;
    }

    public Pusher cancelAfterSeconds(long cancelAfterSeconds) {
        this.cancelAfterSeconds = cancelAfterSeconds;
        return this;
    }

    public long birthTime() {
        return birthTime;
    }

    public Pusher birthTime(long birthTime) {
        this.birthTime = birthTime;
        return this;
    }

    public long upTime() {
        return upTime;
    }

    public Pusher upTime(long upTime) {
        this.upTime = upTime;
        return this;
    }
}

class StatusChecker extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                for (Pusher pusher : Pusher.pushers()) {
                    String streamId = pusher.streamId();
                    boolean shouldBeCancelled = pusher.cancelAfterSeconds() <= 0 ? false :
                            ((System.currentTimeMillis() - pusher.birthTime()
                                    >= pusher.cancelAfterSeconds() * 1000) ? true : false);
                    if (shouldBeCancelled) {
                        System.out.println("pusher should be cancelled");
                        Pusher.stopAndRemove(streamId);
                    } else {
                        if (pusher.keepAlive()) {
                            if (!pusher.isAlive()) {
                                System.out.println("ps " + streamId + " exited, pull up");
                                try {
                                    long now = System.currentTimeMillis();
                                    pusher.pushToJsmpegRelay()
                                            .upTime(now);
                                    Ds.sqlite0.update(
                                            new Sql()
                                                    .update(PusherEntity.class)
                                                    .set("up_time = :up_time")
                                                    .where("stream_id = :stream_id").sql(),
                                            new Param()
                                                    .add("up_time", now)
                                                    .add("stream_id", streamId));
                                } catch (ExecException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(1000 * 15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
