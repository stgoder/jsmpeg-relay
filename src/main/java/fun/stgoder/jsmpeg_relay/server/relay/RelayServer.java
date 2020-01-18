package fun.stgoder.jsmpeg_relay.server.relay;

import fun.stgoder.jsmpeg_relay.common.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class RelayServer {
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ServerBootstrap serverBootstrap;
    private int port;

    {
        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup();
            workerGroup = new EpollEventLoopGroup();
        } else {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
        }
        serverBootstrap = new ServerBootstrap();
    }

    public RelayServer(int port) {
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new RelayChannelInitializer());
        if (Epoll.isAvailable())
            serverBootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
        this.port = port;
    }

    public RelayServer start() throws InterruptedException {
        if (Epoll.isAvailable()) {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            for (int i = 0; i < availableProcessors; i++) {
                serverBootstrap.bind(port).sync();
            }
        } else {
            serverBootstrap.bind(port).sync();
        }
        System.out.println("relay server start port: " + Constants.RELAY_SERVER_PORT);
        return this;
    }

    public RelayServer stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        return this;
    }
}

