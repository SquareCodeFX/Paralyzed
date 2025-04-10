package de.feelix.ocean.client;

import de.feelix.ocean.api.packet.InPacket;
import de.feelix.ocean.api.packet.OutPacket;
import de.feelix.ocean.api.serialization.PacketSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Netty-based client implementation.
 */
public class NettyClient {
    private final String host;
    private final int port;
    private EventLoopGroup group;
    private Channel channel;
    private final ConcurrentMap<String, CompletableFuture<OutPacket>> pendingRequests = new ConcurrentHashMap<>();
    
    /**
     * Creates a new NettyClient with the specified host and port.
     *
     * @param host The server host
     * @param port The server port
     */
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    /**
     * Connects to the server.
     *
     * @throws Exception If an error occurs during connection
     */
    public void connect() throws Exception {
        group = new NioEventLoopGroup();
        
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                            new StringDecoder(CharsetUtil.UTF_8),
                            new StringEncoder(CharsetUtil.UTF_8),
                            new ClientPacketHandler(NettyClient.this)
                        );
                    }
                });
            
            // Connect to the server
            ChannelFuture f = b.connect(host, port).sync();
            channel = f.channel();
            System.out.println("Connected to server at " + host + ":" + port);
        } catch (Exception e) {
            shutdown();
            throw e;
        }
    }
    
    /**
     * Sends a packet to the server and returns a future for the response.
     *
     * @param packet The packet to send
     * @return A future that will be completed with the response
     * @throws Exception If an error occurs during sending
     */
    public CompletableFuture<OutPacket> sendPacket(InPacket packet) throws Exception {
        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException("Client is not connected");
        }
        
        // Create a future for the response
        CompletableFuture<OutPacket> responseFuture = new CompletableFuture<>();
        pendingRequests.put(packet.getTransactionId(), responseFuture);
        
        // Serialize and send the packet
        String json = PacketSerializer.serialize(packet);
        channel.writeAndFlush(json);
        
        return responseFuture;
    }
    
    /**
     * Handles a response packet from the server.
     *
     * @param response The response packet
     */
    void handleResponse(OutPacket response) {
        String transactionId = response.getTransactionId();
        CompletableFuture<OutPacket> future = pendingRequests.remove(transactionId);
        
        if (future != null) {
            future.complete(response);
        } else {
            System.err.println("Received response for unknown transaction ID: " + transactionId);
        }
    }
    
    /**
     * Shuts down the client.
     */
    public void shutdown() {
        if (channel != null) {
            channel.close();
            channel = null;
        }
        
        if (group != null) {
            group.shutdownGracefully();
            group = null;
        }
        
        // Complete all pending requests with an exception
        for (CompletableFuture<OutPacket> future : pendingRequests.values()) {
            future.completeExceptionally(new Exception("Client shutdown"));
        }
        pendingRequests.clear();
        
        System.out.println("Client shut down");
    }
}