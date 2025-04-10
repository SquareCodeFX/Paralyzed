package de.feelix.ocean.server;

/**
 * Server application entry point.
 */
public class ServerApplication {
    private static final int DEFAULT_PORT = 8888;
    
    public static void main(String[] args) {
        try {
            // Parse port from command line arguments if provided
            int port = DEFAULT_PORT;
            if (args.length > 0) {
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port number: " + args[0]);
                    System.err.println("Using default port: " + DEFAULT_PORT);
                }
            }
            
            // Create and start the server
            PacketHandler packetHandler = new SimplePacketHandler();
            NettyServer server = new NettyServer(port, packetHandler);
            
            System.out.println("Starting server on port " + port);
            server.start();
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}