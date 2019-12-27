package fun.stgoder.jsmpeg_relay.server.mpegts;

import fun.stgoder.jsmpeg_relay.server.relay.PlayerGroups;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

class MpegtsHandler extends SimpleChannelInboundHandler<ByteBuf> {
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
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        Channel channel = ctx.channel();
        if (MpegtsGroup.contains(channel)) {
            PlayerGroups.broadcast(MpegtsGroup.getStreamId(channel), msg);
        } else {
            try {
                System.out.println("try to get http uri");
                int readableBytes = msg.readableBytes();
                byte[] bytes = new byte[readableBytes];
                msg.readBytes(bytes);
                String str = new String(bytes, Charset.forName("utf-8"));
                System.out.println(str);
                String[] lines = str.split("\n");
                String firstLine = lines[0];
                String[] methodUriHttpVersion = firstLine.split(" ");
                String uri = methodUriHttpVersion[1];
                String streamId = uri.replace("/", "");
                System.out.println("pusher streamId:" + streamId);
                MpegtsGroup.addChannel(streamId, channel);
            } catch (Exception e) {
                e.printStackTrace();
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
    }
}
