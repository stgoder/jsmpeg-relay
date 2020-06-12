package fun.stgoder.jsmpeg_relay;

import fun.stgoder.jsmpeg_relay.common.Constants;
import fun.stgoder.jsmpeg_relay.common.db.Ds;
import fun.stgoder.jsmpeg_relay.common.exception.BaseException;
import fun.stgoder.jsmpeg_relay.ps.pusher.Pusher;
import fun.stgoder.jsmpeg_relay.server.mpegts.MpegtsServer;
import fun.stgoder.jsmpeg_relay.server.relay.RelayServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Start {

    public static void main(String[] args) throws InterruptedException, BaseException {
        SpringApplication.run(Start.class, args);
        Ds.initSqlite0();
        new MpegtsServer(Constants.MPEGTS_SERVER_PORT).start();
        new RelayServer(Constants.RELAY_SERVER_PORT).start();
        Pusher.loadFromDB();
    }
}