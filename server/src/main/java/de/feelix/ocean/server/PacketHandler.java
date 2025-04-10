package de.feelix.ocean.server;

import de.feelix.ocean.api.packet.InPacket;
import de.feelix.ocean.api.packet.OutPacket;
import de.feelix.ocean.api.packet.PacketType;
import de.feelix.ocean.server.handler.PacketTypeHandler;

/**
 * Interface for handling packets received by the server.
 */
public interface PacketHandler {
    /**
     * Handles an incoming packet and generates a response.
     *
     * @param packet The incoming packet
     * @return The response packet
     */
    OutPacket handlePacket(InPacket packet);

    /**
     * Registers a handler for a specific packet type.
     *
     * @param type The packet type
     * @param handler The handler for the packet type
     * @param <T> The packet type
     */
    <T extends InPacket> void registerHandler(PacketType type, PacketTypeHandler<T> handler);
}
