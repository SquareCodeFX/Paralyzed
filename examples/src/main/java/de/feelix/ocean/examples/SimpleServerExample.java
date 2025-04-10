package de.feelix.ocean.examples;

import de.feelix.ocean.api.packet.PacketType;
import de.feelix.ocean.server.NettyServer;
import de.feelix.ocean.server.PacketHandler;
import de.feelix.ocean.server.SimplePacketHandler;
import de.feelix.ocean.server.config.ServerConfig;
import de.feelix.ocean.server.handler.SimpleInPacketHandler;
import de.feelix.ocean.server.session.SessionManager;

import java.time.Duration;

/**
 * A simple example demonstrating how to use the Paralyzed server.
 */
public class SimpleServerExample {

    public static void main(String[] args) {
        try {
            // Create a server configuration
            ServerConfig config = new ServerConfig()
                .setPort(8888)
                .setBossThreads(1)
                .setWorkerThreads(4)
                .setSessionTimeout(Duration.ofMinutes(30));

            // Create a packet handler
            PacketHandler packetHandler = new SimplePacketHandler();
            
            // Register custom packet handlers if needed
            // For this example, we'll use the default SimpleInPacketHandler
            // which is already registered in SimplePacketHandler
            
            // Create a session manager with the configuration
            SessionManager sessionManager = new SessionManager(config);
            
            // Create the server
            NettyServer server = new NettyServer(config.getPort(), packetHandler, sessionManager);
            
            System.out.println("Starting server on port " + config.getPort());
            
            // Start the server
            server.start();
            
            // The server will run until an exception occurs or it's manually stopped
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}