package de.feelix.ocean.api.serialization;

import de.feelix.ocean.api.packet.Packet;
import de.feelix.ocean.api.packet.PacketType;
import de.feelix.ocean.api.packet.OutPacket;
import de.feelix.ocean.api.packet.SimpleInPacket;

import java.util.EnumMap;
import java.util.Map;

/**
 * Registry for packet types.
 * This class maps packet types to their respective classes.
 */
public class PacketRegistry {
    private static final Map<PacketType, Class<? extends Packet>> PACKET_TYPES = new EnumMap<>(PacketType.class);

    static {
        // Register built-in packet types
        register(PacketType.OUT_PACKET, OutPacket.class);
        register(PacketType.SIMPLE_IN_PACKET, SimpleInPacket.class);
    }

    /**
     * Registers a packet type with its class.
     *
     * @param type The packet type
     * @param packetClass The packet class
     */
    public static void register(PacketType type, Class<? extends Packet> packetClass) {
        PACKET_TYPES.put(type, packetClass);
    }

    /**
     * Gets the class for a packet type.
     *
     * @param type The packet type
     * @return The packet class, or null if not found
     */
    public static Class<? extends Packet> getPacketClass(PacketType type) {
        return PACKET_TYPES.get(type);
    }

    /**
     * Gets the class for a packet type identifier.
     *
     * @param typeIdentifier The packet type identifier
     * @return The packet class, or null if not found
     */
    public static Class<? extends Packet> getPacketClassByIdentifier(String typeIdentifier) {
        PacketType type = PacketType.fromIdentifier(typeIdentifier);
        return type != null ? PACKET_TYPES.get(type) : null;
    }
}
