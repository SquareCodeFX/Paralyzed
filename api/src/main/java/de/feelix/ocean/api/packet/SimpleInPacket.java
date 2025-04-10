package de.feelix.ocean.api.packet;

import de.feelix.ocean.api.validation.ValidationException;

/**
 * A simple implementation of the InPacket interface.
 * This is an example of how to create custom packets.
 */
public class SimpleInPacket implements InPacket {
    private final String transactionId;
    private final String message;

    /**
     * Creates a new SimpleInPacket with the specified transaction ID and message.
     *
     * @param transactionId The transaction ID for correlating with the response
     * @param message The message to send to the server
     */
    public SimpleInPacket(String transactionId, String message) {
        this.transactionId = transactionId;
        this.message = message;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Gets the message contained in this packet.
     *
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    @Override
    public PacketType getType() {
        return PacketType.SIMPLE_IN_PACKET;
    }

    @Override
    public void validate() throws ValidationException {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new ValidationException("Transaction ID cannot be null or empty");
        }
        if (message == null) {
            throw new ValidationException("Message cannot be null");
        }
    }
}
