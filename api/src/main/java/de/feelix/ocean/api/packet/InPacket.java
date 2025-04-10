package de.feelix.ocean.api.packet;

/**
 * Interface for packets sent from client to server.
 * All incoming packets should implement this interface.
 */
public interface InPacket extends Packet {
    /**
     * Gets the transaction ID for this packet.
     * The transaction ID is used to correlate requests and responses.
     *
     * @return The transaction ID
     */
    String getTransactionId();
}