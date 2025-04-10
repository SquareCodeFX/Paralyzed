package de.feelix.ocean.examples;

import de.feelix.ocean.api.packet.InPacket;
import de.feelix.ocean.api.packet.OutPacket;
import de.feelix.ocean.api.packet.PacketType;
import de.feelix.ocean.api.serialization.PacketRegistry;
import de.feelix.ocean.api.validation.ValidationException;
import de.feelix.ocean.server.PacketHandler;
import de.feelix.ocean.server.SimplePacketHandler;
import de.feelix.ocean.server.handler.PacketTypeHandler;

/**
 * Example demonstrating how to create and use custom packet types.
 */
public class CustomPacketExample {

    /**
     * Custom packet type for the example.
     */
    public enum CustomPacketType {
        ECHO_PACKET("ECHO_PACKET"),
        MATH_PACKET("MATH_PACKET");
        
        private final String identifier;
        
        CustomPacketType(String identifier) {
            this.identifier = identifier;
        }
        
        public String getIdentifier() {
            return identifier;
        }
        
        public static CustomPacketType fromIdentifier(String identifier) {
            for (CustomPacketType type : values()) {
                if (type.getIdentifier().equals(identifier)) {
                    return type;
                }
            }
            return null;
        }
    }
    
    /**
     * Custom packet for echoing text.
     */
    public static class EchoPacket implements InPacket {
        private final String transactionId;
        private final String text;
        
        public EchoPacket(String transactionId, String text) {
            this.transactionId = transactionId;
            this.text = text;
        }
        
        @Override
        public String getTransactionId() {
            return transactionId;
        }
        
        public String getText() {
            return text;
        }
        
        @Override
        public PacketType getType() {
            return PacketType.valueOf(CustomPacketType.ECHO_PACKET.name());
        }
        
        @Override
        public void validate() throws ValidationException {
            if (transactionId == null || transactionId.isEmpty()) {
                throw new ValidationException("Transaction ID cannot be null or empty");
            }
            if (text == null) {
                throw new ValidationException("Text cannot be null");
            }
        }
    }
    
    /**
     * Custom packet for performing math operations.
     */
    public static class MathPacket implements InPacket {
        private final String transactionId;
        private final double a;
        private final double b;
        private final String operation;
        
        public MathPacket(String transactionId, double a, double b, String operation) {
            this.transactionId = transactionId;
            this.a = a;
            this.b = b;
            this.operation = operation;
        }
        
        @Override
        public String getTransactionId() {
            return transactionId;
        }
        
        public double getA() {
            return a;
        }
        
        public double getB() {
            return b;
        }
        
        public String getOperation() {
            return operation;
        }
        
        @Override
        public PacketType getType() {
            return PacketType.valueOf(CustomPacketType.MATH_PACKET.name());
        }
        
        @Override
        public void validate() throws ValidationException {
            if (transactionId == null || transactionId.isEmpty()) {
                throw new ValidationException("Transaction ID cannot be null or empty");
            }
            if (operation == null || operation.isEmpty()) {
                throw new ValidationException("Operation cannot be null or empty");
            }
            if (!operation.equals("+") && !operation.equals("-") && 
                !operation.equals("*") && !operation.equals("/")) {
                throw new ValidationException("Invalid operation: " + operation);
            }
            if (operation.equals("/") && b == 0) {
                throw new ValidationException("Division by zero");
            }
        }
    }
    
    /**
     * Handler for EchoPacket.
     */
    public static class EchoPacketHandler implements PacketTypeHandler<EchoPacket> {
        @Override
        public OutPacket handle(EchoPacket packet) {
            try {
                // Validate the packet
                packet.validate();
                
                // Echo the text
                String response = "Echo: " + packet.getText();
                
                // Return a success response
                return OutPacket.Builder.success(packet.getTransactionId(), response);
            } catch (Exception e) {
                // Return an error response
                return OutPacket.Builder.error(packet.getTransactionId(), "Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Handler for MathPacket.
     */
    public static class MathPacketHandler implements PacketTypeHandler<MathPacket> {
        @Override
        public OutPacket handle(MathPacket packet) {
            try {
                // Validate the packet
                packet.validate();
                
                // Perform the operation
                double result;
                switch (packet.getOperation()) {
                    case "+":
                        result = packet.getA() + packet.getB();
                        break;
                    case "-":
                        result = packet.getA() - packet.getB();
                        break;
                    case "*":
                        result = packet.getA() * packet.getB();
                        break;
                    case "/":
                        result = packet.getA() / packet.getB();
                        break;
                    default:
                        throw new ValidationException("Invalid operation: " + packet.getOperation());
                }
                
                // Return a success response
                String response = packet.getA() + " " + packet.getOperation() + " " + packet.getB() + " = " + result;
                return OutPacket.Builder.success(packet.getTransactionId(), response);
            } catch (Exception e) {
                // Return an error response
                return OutPacket.Builder.error(packet.getTransactionId(), "Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Example of how to register custom packet types and handlers.
     */
    public static void registerCustomPackets() {
        // Add custom packet types to the PacketType enum
        // This would normally be done by extending the PacketType enum
        // For this example, we'll assume the PacketType enum has been extended
        
        // Register custom packet classes
        PacketRegistry.register(PacketType.valueOf(CustomPacketType.ECHO_PACKET.name()), EchoPacket.class);
        PacketRegistry.register(PacketType.valueOf(CustomPacketType.MATH_PACKET.name()), MathPacket.class);
    }
    
    /**
     * Example of how to register custom packet handlers.
     */
    public static void registerCustomHandlers(PacketHandler packetHandler) {
        // Register custom packet handlers
        packetHandler.registerHandler(PacketType.valueOf(CustomPacketType.ECHO_PACKET.name()), new EchoPacketHandler());
        packetHandler.registerHandler(PacketType.valueOf(CustomPacketType.MATH_PACKET.name()), new MathPacketHandler());
    }
    
    /**
     * Example of how to use the custom packets in a server.
     */
    public static void main(String[] args) {
        // Register custom packet types
        registerCustomPackets();
        
        // Create a packet handler
        PacketHandler packetHandler = new SimplePacketHandler();
        
        // Register custom packet handlers
        registerCustomHandlers(packetHandler);
        
        // Now you can use the custom packets and handlers in your server
        System.out.println("Custom packets and handlers registered.");
        System.out.println("You can now use EchoPacket and MathPacket in your application.");
    }
}