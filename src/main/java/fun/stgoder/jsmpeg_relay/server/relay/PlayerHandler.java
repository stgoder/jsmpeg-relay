package fun.stgoder.jsmpeg_relay.server.relay;

import fun.stgoder.jsmpeg_relay.common.Constants;
import fun.stgoder.jsmpeg_relay.common.util.Util;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;

import java.io.IOException;

public class PlayerHandler extends SimpleChannelInboundHandler<Object> {
    private WebSocketServerHandshaker handshaker;
    private WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
            "ws://" + Constants.localIpv4 + ":" + Constants.RELAY_SERVER_PORT,
            "null", true);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ws connect ctx:" + ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) throws Exception {
        if (handshaker == null) {
            handshaker = wsFactory.newHandshaker(req);
        }
        handshaker.handshake(ctx.channel(), req);
        String uri = req.uri();
        String stream = Util.getParamFromUri(uri, "streamId");
        PlayerGroups.addPlayer(stream, ctx.channel());
    }

    private void handleWebSocketFrame(ChannelHandlerContext channelHandlerContext, WebSocketFrame frame) throws IOException {
        Channel channel = channelHandlerContext.channel();
        if (frame instanceof CloseWebSocketFrame) {
            CloseWebSocketFrame cwsf = (CloseWebSocketFrame) frame;
            handshaker.close(channel, cwsf.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            channel.write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof TextWebSocketFrame) {

        }
        if (frame instanceof BinaryWebSocketFrame) {

        }
    }
}
