package de.feelix.ocean.api.packet;

import de.feelix.ocean.api.validation.Validatable;

/**
 * Base interface for all packets in the communication system.
 * All packets should implement this interface.
 */
public interface Packet extends Validatable {
    /**
     * Gets the packet type.
     * This is used for packet serialization and deserialization.
     *
     * @return The packet type
     */
    PacketType getType();
}
