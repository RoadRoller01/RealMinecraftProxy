package me.roadroller01.RealMinecraftProxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.buffer.Unpooled;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClientConnectionHandler extends ChannelInboundHandlerAdapter { // (1)

    private final static Logger LOGGER = LogManager.getLogger();

    private final String serverAddress;
    private final int P2S;

    private Channel serverChannel;

    public ClientConnectionHandler(String serverAddress, int P2S) {
        this.serverAddress = serverAddress;
        this.P2S = P2S;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel clientChannel = ctx.channel();
        Bootstrap b = new Bootstrap();
        b.group(clientChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new ProxyClient(clientChannel))
                .option(ChannelOption.AUTO_READ, false);
        ChannelFuture f = b.connect(serverAddress, P2S);
        serverChannel = f.channel();
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    // connection complete start to read first data
                    clientChannel.read();

                } else {
                    // Close the connection if the connection attempt has failed.
                    clientChannel.close();
                }
            }
        });

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (serverChannel.isActive()) {
            serverChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
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
        }
//        System.out.println(msg.toString(CharsetUtil.US_ASCII));
//        serverChannel.writeAndFlush(msg);
//            while (msg.isReadable()) {
//                System.out.print((char) msg.readByte());
//                System.out.flush();
//            }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (serverChannel != null) {
            closeOnFlush(serverChannel);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            // TODO it should send minecraft disconnect packet
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}