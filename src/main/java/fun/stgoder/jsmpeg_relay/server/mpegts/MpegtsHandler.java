package fun.stgoder.jsmpeg_relay.server.mpegts;

import fun.stgoder.jsmpeg_relay.server.relay.PlayerGroups;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

class MpegtsHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("pusher connect ctx:" + ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        MpegtsGroup.removeChannel(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        Channel channel = ctx.channel();
        if (msg instanceof HttpContent) {
            if (MpegtsGroup.contains(channel)) {
                PlayerGroups.broadcast(MpegtsGroup.getStreamId(channel), ((HttpContent) msg).content());
            }
        } else if (msg instanceof HttpRequest) {
            String uri = ((HttpRequest) msg).uri();
            String streamId = uri.replace("/", "");
            System.out.println("pusher streamId:" + streamId);
            if (!MpegtsGroup.containsStreamId(streamId)) {
                MpegtsGroup.addChannel(streamId, channel);
            } else {
                ctx.close();
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        MpegtsGroup.removeChannel(ctx.channel());
    }
}
