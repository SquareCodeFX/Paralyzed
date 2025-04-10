package de.feelix.ocean.server.handler;

import de.feelix.ocean.api.packet.InPacket;
import de.feelix.ocean.api.packet.PacketType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Registry for packet handlers.
 * This class maps packet types to their respective handlers.
 */
public class PacketHandlerRegistry {
    private static final Map<PacketType, PacketTypeHandler<?>> HANDLERS = new EnumMap<>(PacketType.class);
    
    /**
     * Registers a handler for a packet type.
     *
     * @param type The packet type
     * @param handler The handler for the packet type
     * @param <T> The packet type
     */
    public static <T extends InPacket> void register(PacketType type, PacketTypeHandler<T> handler) {
        HANDLERS.put(type, handler);
    }
    
    /**
     * Gets the handler for a packet type.
     *
     * @param type The packet type
     * @return The handler for the packet type, or null if not found
     */
    @SuppressWarnings("unchecked")
    public static <T extends InPacket> PacketTypeHandler<T> getHandler(PacketType type) {
        return (PacketTypeHandler<T>) HANDLERS.get(type);
    }
    
    /**
     * Gets the handler for a packet.
     *
     * @param packet The packet
     * @return The handler for the packet, or null if not found
     */
    @SuppressWarnings("unchecked")
    public static <T extends InPacket> PacketTypeHandler<T> getHandler(T packet) {
        return (PacketTypeHandler<T>) HANDLERS.get(packet.getType());
    }
}