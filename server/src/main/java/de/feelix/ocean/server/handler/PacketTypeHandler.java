package de.feelix.ocean.server.handler;

import de.feelix.ocean.api.packet.InPacket;
import de.feelix.ocean.api.packet.OutPacket;

/**
 * Interface for handling specific types of packets.
 *
 * @param <T> The type of packet this handler can process
 */
public interface PacketTypeHandler<T extends InPacket> {
    /**
     * Handles a packet and generates a response.
     *
     * @param packet The packet to handle
     * @return The response packet
     */
    OutPacket handle(T packet);
}