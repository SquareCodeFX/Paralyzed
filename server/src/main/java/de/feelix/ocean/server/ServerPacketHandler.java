package de.feelix.ocean.server;

import de.feelix.ocean.api.packet.InPacket;
import de.feelix.ocean.api.packet.OutPacket;
import de.feelix.ocean.api.serialization.PacketSerializer;
import de.feelix.ocean.server.session.ClientSession;
import de.feelix.ocean.server.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Netty channel handler for processing packets.
 */
public class ServerPacketHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger LOGGER = Logger.getLogger(ServerPacketHandler.class.getName());

    private final PacketHandler packetHandler;
    private final SessionManager sessionManager;

    /**
     * Creates a new ServerPacketHandler with the specified packet handler and session manager.
     *
     * @param packetHandler The handler for processing packets
     * @param sessionManager The manager for client sessions
     */
    public ServerPacketHandler(PacketHandler packetHandler, SessionManager sessionManager) {
        this.packetHandler = packetHandler;
        this.sessionManager = sessionManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Create a new session for the client
        ClientSession session = sessionManager.createSession(ctx.channel());
        LOGGER.info("Client connected: " + ctx.channel().remoteAddress() + " (Session ID: " + session.getSessionId() + ")");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // Remove the session when the client disconnects
        ClientSession session = sessionManager.removeSession(ctx.channel());
        if (session != null) {
            LOGGER.info("Client disconnected: " + ctx.channel().remoteAddress() + " (Session ID: " + session.getSessionId() + ")");
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        try {
            // Get the client session
            ClientSession session = sessionManager.getSession(ctx.channel());
            if (session == null) {
                LOGGER.warning("Received message from unknown client: " + ctx.channel().remoteAddress());
                return;
            }

            // Update the last activity time
            session.updateLastActivityTime();

            // Deserialize the incoming packet
            InPacket inPacket = (InPacket) PacketSerializer.deserialize(msg);

            // Process the packet and get the response
            OutPacket outPacket = packetHandler.handlePacket(inPacket);

            // Serialize and send the response
            String response = PacketSerializer.serialize(outPacket);
            ctx.writeAndFlush(response);

            LOGGER.fine("Processed packet from client: " + ctx.channel().remoteAddress() + 
                " (Session ID: " + session.getSessionId() + ", Transaction ID: " + inPacket.getTransactionId() + ")");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing packet: " + e.getMessage(), e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.log(Level.SEVERE, "Channel exception: " + cause.getMessage(), cause);
        ctx.close();
    }
}
