package de.feelix.ocean.client;

import de.feelix.ocean.api.packet.OutPacket;
import de.feelix.ocean.api.packet.SimpleInPacket;
import de.feelix.ocean.api.util.TransactionIdGenerator;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Client application entry point.
 */
public class ClientApplication {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8888;
    
    public static void main(String[] args) {
        try {
            // Parse host and port from command line arguments if provided
            String host = DEFAULT_HOST;
            int port = DEFAULT_PORT;
            
            if (args.length > 0) {
                host = args[0];
            }
            
            if (args.length > 1) {
                try {
                    port = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port number: " + args[1]);
                    System.err.println("Using default port: " + DEFAULT_PORT);
                }
            }
            
            // Create and connect the client
            NettyClient client = new NettyClient(host, port);
            System.out.println("Connecting to server at " + host + ":" + port);
            client.connect();
            
            // Interactive mode
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter messages to send to the server (type 'exit' to quit):");
            
            String input;
            while (!(input = scanner.nextLine()).equalsIgnoreCase("exit")) {
                // Generate a transaction ID
                String transactionId = TransactionIdGenerator.generateId();
                
                // Create and send a packet
                SimpleInPacket packet = new SimpleInPacket(transactionId, input);
                CompletableFuture<OutPacket> future = client.sendPacket(packet);
                
                try {
                    // Wait for the response
                    OutPacket response = future.get();
                    System.out.println("Response: " + response.getResponse());
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println("Error receiving response: " + e.getMessage());
                }
            }
            
            // Shutdown the client
            client.shutdown();
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}