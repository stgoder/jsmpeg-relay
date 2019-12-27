package fun.stgoder.jsmpeg_relay.server.mpegts;

import fun.stgoder.jsmpeg_relay.common.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class MpegtsServer {
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup wokerGroup = new NioEventLoopGroup();
    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    private int port;

    public MpegtsServer(int port) {
        serverBootstrap.group(bossGroup, wokerGroup).channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new MpegtsChannelInitializer());
        this.port = port;
    }

    public MpegtsServer start() throws InterruptedException {
        serverBootstrap.bind(port).sync();
        System.out.println("mpegts server start port: " + Constants.MPEGTS_SERVER_PORT);
        return this;
    }

    public MpegtsServer stop() {
        bossGroup.shutdownGracefully();
        wokerGroup.shutdownGracefully();
        return this;
    }
}


