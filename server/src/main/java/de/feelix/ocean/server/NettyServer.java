package de.feelix.ocean.server;

import de.feelix.ocean.server.session.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.util.logging.Logger;

/**
 * Netty-based server implementation.
 */
public class NettyServer {
    private static final Logger LOGGER = Logger.getLogger(NettyServer.class.getName());

    private final int port;
    private final PacketHandler packetHandler;
    private final SessionManager sessionManager;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    /**
     * Creates a new NettyServer with the specified port and packet handler.
     *
     * @param port The port to listen on
     * @param packetHandler The handler for processing packets
     */
    public NettyServer(int port, PacketHandler packetHandler) {
        this.port = port;
        this.packetHandler = packetHandler;
        this.sessionManager = new SessionManager();
    }

    /**
     * Creates a new NettyServer with the specified port, packet handler, and session manager.
     *
     * @param port The port to listen on
     * @param packetHandler The handler for processing packets
     * @param sessionManager The manager for client sessions
     */
    public NettyServer(int port, PacketHandler packetHandler, SessionManager sessionManager) {
        this.port = port;
        this.packetHandler = packetHandler;
        this.sessionManager = sessionManager;
    }

    /**
     * Gets the session manager.
     *
     * @return The session manager
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Starts the server.
     *
     * @throws Exception If an error occurs during startup
     */
    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                            new StringDecoder(CharsetUtil.UTF_8),
                            new StringEncoder(CharsetUtil.UTF_8),
                            new ServerPacketHandler(packetHandler, sessionManager)
                        );
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections
            ChannelFuture f = b.bind(port).sync();
            LOGGER.info("Server started on port " + port);

            // Wait until the server socket is closed
            f.channel().closeFuture().sync();
        } finally {
            shutdown();
        }
    }

    /**
     * Shuts down the server.
     */
    public void shutdown() {
        LOGGER.info("Shutting down server...");

        // Shutdown the session manager
        sessionManager.shutdown();

        // Shutdown the event loop groups
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

        LOGGER.info("Server shut down");
    }
}
