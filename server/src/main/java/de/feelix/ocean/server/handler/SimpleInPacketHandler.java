package de.feelix.ocean.server.handler;

import de.feelix.ocean.api.packet.OutPacket;
import de.feelix.ocean.api.packet.SimpleInPacket;

/**
 * Handler for SimpleInPacket instances.
 */
public class SimpleInPacketHandler implements PacketTypeHandler<SimpleInPacket> {
    
    @Override
    public OutPacket handle(SimpleInPacket packet) {
        try {
            // Validate the packet
            packet.validate();
            
            // Get the transaction ID and message
            String transactionId = packet.getTransactionId();
            String message = packet.getMessage();
            
            // Process the message
            String response = processMessage(message);
            
            // Create and return a success response
            return OutPacket.Builder.success(transactionId, response);
        } catch (Exception e) {
            // Create and return an error response
            return OutPacket.Builder.error(packet.getTransactionId(), "Error processing packet: " + e.getMessage());
        }
    }
    
    /**
     * Processes a message and generates a response.
     *
     * @param message The message to process
     * @return The response
     */
    private String processMessage(String message) {
        // This is a simple example implementation
        return "Server received: " + message;
    }
}