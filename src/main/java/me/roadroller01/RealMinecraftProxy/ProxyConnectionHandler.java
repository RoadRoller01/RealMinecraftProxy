package me.roadroller01.RealMinecraftProxy;

import io.netty.channel.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProxyConnectionHandler extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = LogManager.getLogger();
    private final Channel clientChannel;
    public ProxyConnectionHandler(Channel clientChannel) {
        this.clientChannel = clientChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        clientChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    // was able to flush out data, start to read the next chunk
                    ctx.channel().read();
                } else {
                    future.channel().close();
                }
            }
        });
        // Discard the received data silently.
//        ((ByteBuf) msg).release(); // (3)
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ClientConnectionHandler.closeOnFlush(clientChannel);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ClientConnectionHandler.closeOnFlush(ctx.channel());
    }

//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
//        // Close the connection when an exception is raised.
//        cause.printStackTrace();
//        ctx.close();
//    }
}
