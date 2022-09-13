package me.roadroller01.RealMinecraftProxy;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProxyServer {

    private final static Logger LOGGER = LogManager.getLogger();

    private final String serverAddress;

    /**
     * proxy to server port
     */
    private final int P2S;

    /**
     * client to proxy port
     */
    private final int C2P;

    public ProxyServer(String serverAddress, int P2S, int C2P) {
        this.serverAddress = serverAddress;
        this.P2S = P2S;
        this.C2P = C2P;
    }


    public void run() throws Exception {
        LOGGER.info("Proxying *:" + C2P + " to " + serverAddress + ':' + P2S + " ...");
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<Channel>() { // (4)
                        @Override
                        public void initChannel(Channel ch){
                            try {
                                ch.config().setOption(ChannelOption.TCP_NODELAY, true);
                            }
                            catch (ChannelException channelException) {
                                // empty catch block
                            }

                            ch.pipeline().addLast(
                                    new LoggingHandler(LogLevel.INFO),
                                    new ClientConnectionHandler(serverAddress,P2S)
                            );
                        }
                    })
                      .childOption(ChannelOption.AUTO_READ, false);


            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(C2P).sync(); // (7)

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
