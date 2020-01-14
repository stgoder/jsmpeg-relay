package fun.stgoder.jsmpeg_relay.server.mpegts;

import fun.stgoder.jsmpeg_relay.common.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class MpegtsServer {
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup wokerGroup;
    private final ServerBootstrap serverBootstrap;
    private int port;

    {
        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup();
            wokerGroup = new EpollEventLoopGroup();
        } else {
            bossGroup = new NioEventLoopGroup();
            wokerGroup = new NioEventLoopGroup();
        }
        serverBootstrap = new ServerBootstrap();
    }

    public MpegtsServer(int port) {
        serverBootstrap.group(bossGroup, wokerGroup)
                .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new MpegtsChannelInitializer());
        serverBootstrap.option(ChannelOption.SO_SNDBUF, 1024*256);
        serverBootstrap.option(ChannelOption.SO_RCVBUF, 1024*256);
        serverBootstrap.option(ChannelOption.TCP_NODELAY, true);
        if (Epoll.isAvailable())
            serverBootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
        this.port = port;
    }

    public MpegtsServer start() throws InterruptedException {
        if (Epoll.isAvailable()) {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            for (int i = 0; i < availableProcessors; i++) {
                serverBootstrap.bind(port).sync();
            }
        } else {
            serverBootstrap.bind(port).sync();
        }
        System.out.println("mpegts server start port: " + Constants.MPEGTS_SERVER_PORT);
        return this;
    }

    public MpegtsServer stop() {
        bossGroup.shutdownGracefully();
        wokerGroup.shutdownGracefully();
        return this;
    }
}


