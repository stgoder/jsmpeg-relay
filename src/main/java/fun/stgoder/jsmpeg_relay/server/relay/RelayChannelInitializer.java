package fun.stgoder.jsmpeg_relay.server.relay;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class RelayChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("HttpServerCodec", new HttpServerCodec());
        pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(65535));
        pipeline.addLast("ChunkedWriteHandler", new ChunkedWriteHandler());
        pipeline.addLast("PlayerHandler", new PlayerHandler());
    }
}
