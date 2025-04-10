package de.feelix.ocean.api.packet;

/**
 * Enumeration of packet types.
 * This provides type safety and centralized management of packet types.
 */
public enum PacketType {
    /**
     * Packet type for OutPacket.
     */
    OUT_PACKET("OUT_PACKET"),
    
    /**
     * Packet type for SimpleInPacket.
     */
    SIMPLE_IN_PACKET("SIMPLE_IN_PACKET");
    
    private final String identifier;
    
    /**
     * Creates a new PacketType with the specified identifier.
     *
     * @param identifier The string identifier for the packet type
     */
    PacketType(String identifier) {
        this.identifier = identifier;
    }
    
    /**
     * Gets the string identifier for this packet type.
     *
     * @return The string identifier
     */
    public String getIdentifier() {
        return identifier;
    }
    
    /**
     * Gets a PacketType by its string identifier.
     *
     * @param identifier The string identifier
     * @return The corresponding PacketType, or null if not found
     */
    public static PacketType fromIdentifier(String identifier) {
        for (PacketType type : values()) {
            if (type.getIdentifier().equals(identifier)) {
                return type;
            }
        }
        return null;
    }
}