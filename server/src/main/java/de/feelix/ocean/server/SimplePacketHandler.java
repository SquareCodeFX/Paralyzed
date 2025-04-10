package de.feelix.ocean.server;

import de.feelix.ocean.api.packet.InPacket;
import de.feelix.ocean.api.packet.OutPacket;
import de.feelix.ocean.api.packet.PacketType;
import de.feelix.ocean.api.packet.SimpleInPacket;
import de.feelix.ocean.server.handler.PacketHandlerRegistry;
import de.feelix.ocean.server.handler.PacketTypeHandler;
import de.feelix.ocean.server.handler.SimpleInPacketHandler;

/**
 * A simple implementation of the PacketHandler interface.
 * This handler uses a registry of type-specific handlers to process packets.
 */
public class SimplePacketHandler implements PacketHandler {

    /**
     * Creates a new SimplePacketHandler and registers the default handlers.
     */
    public SimplePacketHandler() {
        // Register default handlers
        registerHandler(PacketType.SIMPLE_IN_PACKET, new SimpleInPacketHandler());
    }

    @Override
    public OutPacket handlePacket(InPacket packet) {
        try {
            // Validate the packet
            packet.validate();

            // Get the handler for the packet type
            PacketTypeHandler<InPacket> handler = PacketHandlerRegistry.getHandler(packet);

            if (handler != null) {
                // Handle the packet using the appropriate handler
                return handler.handle(packet);
            } else {
                // No handler found for this packet type
                return OutPacket.Builder.error(
                    packet.getTransactionId(),
                    "No handler found for packet type: " + packet.getType()
                );
            }
        } catch (Exception e) {
            // Handle any exceptions
            return OutPacket.Builder.error(
                packet.getTransactionId(),
                "Error processing packet: " + e.getMessage()
            );
        }
    }

    @Override
    public <T extends InPacket> void registerHandler(PacketType type, PacketTypeHandler<T> handler) {
        PacketHandlerRegistry.register(type, handler);
    }
}
