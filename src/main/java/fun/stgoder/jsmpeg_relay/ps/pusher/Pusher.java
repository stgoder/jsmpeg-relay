package fun.stgoder.jsmpeg_relay.ps.pusher;

import fun.stgoder.jsmpeg_relay.common.Constants;
import fun.stgoder.jsmpeg_relay.common.OS;
import fun.stgoder.jsmpeg_relay.common.exception.ExecException;
import fun.stgoder.jsmpeg_relay.ps.Cmd;
import fun.stgoder.jsmpeg_relay.ps.Ps;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
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
        Pusher pusher = new Pusher(streamId, source)
                .keepAlive(keepAlive)
                .cancelAfterSeconds(cancelAfterSeconds)
                .pushToJsmpegRelay()
                .birthTime(System.currentTimeMillis())
                .upTime(System.currentTimeMillis());
        pushers.put(streamId, pusher);
    }

    public static void stopAndRemove(String key) {
        Pusher pusher = pushers.get(key);
        if (pusher == null)
            return;
        pusher.cleanup();
        pushers.remove(key);
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
                cmd.add("-rtsp_transport");
                cmd.add("tcp");
            }
            cmd.add("-re")
                    .add("-i")
                    .add(source)
                    .add("-f")
                    .add("mpegts")
                    .add("-codec:v")
                    .add("mpeg1video")
                    .add("-stats")
                    .add("-r")
                    .add("25")
                    .add("-b:v")
                    .add("1000k")
                    .add("http://127.0.0.1:" + Constants.MPEGTS_SERVER_PORT + "/" + streamId)
                    .add("-loglevel")
                    .add("error");
        }
        if (OS.isWIN()) {
            cmd.add(Constants.FFMPEG_PATH);
            if (!isFile) {
                cmd.add("-rtsp_transport");
                cmd.add("tcp");
            }
            cmd.add("-re")
                    .add("-i")
                    .add(source)
                    .add("-f")
                    .add("mpegts")
                    .add("-codec:v")
                    .add("mpeg1video")
                    .add("-stats")
                    .add("-r")
                    .add("25")
                    .add("-b:v")
                    .add("1000k")
                    .add("http://127.0.0.1:" + Constants.MPEGTS_SERVER_PORT + "/" + streamId)
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
                                    pusher.pushToJsmpegRelay()
                                            .upTime(System.currentTimeMillis());
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
                sleep(1000 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
