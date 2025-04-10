package de.feelix.ocean.client;

import de.feelix.ocean.api.packet.OutPacket;
import de.feelix.ocean.api.serialization.PacketSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Netty channel handler for processing responses from the server.
 */
public class ClientPacketHandler extends SimpleChannelInboundHandler<String> {
    private final NettyClient client;
    
    /**
     * Creates a new ClientPacketHandler with the specified client.
     *
     * @param client The client instance
     */
    public ClientPacketHandler(NettyClient client) {
        this.client = client;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        try {
            // Deserialize the response packet
            OutPacket outPacket = (OutPacket) PacketSerializer.deserialize(msg);
            
            // Handle the response
            client.handleResponse(outPacket);
        } catch (Exception e) {
            System.err.println("Error processing response: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Channel exception: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Disconnected from server");
        client.shutdown();
    }
}