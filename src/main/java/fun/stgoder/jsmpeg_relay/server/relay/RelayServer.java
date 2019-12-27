package fun.stgoder.jsmpeg_relay.server.relay;

import fun.stgoder.jsmpeg_relay.common.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class RelayServer {
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup wokerGroup = new NioEventLoopGroup();
    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    private int port;

    public RelayServer(int port) {
        serverBootstrap.group(bossGroup, wokerGroup).channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new RelayChannelInitializer());
        this.port = port;
    }

    public RelayServer start() throws InterruptedException {
        serverBootstrap.bind(port).sync();
        System.out.println("relay server start port: " + Constants.RELAY_SERVER_PORT);
        return this;
    }

    public RelayServer stop() {
        bossGroup.shutdownGracefully();
        wokerGroup.shutdownGracefully();
        return this;
    }
}

