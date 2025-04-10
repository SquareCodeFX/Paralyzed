package de.feelix.ocean.examples;

import de.feelix.ocean.api.packet.OutPacket;
import de.feelix.ocean.api.packet.SimpleInPacket;
import de.feelix.ocean.api.util.TransactionIdGenerator;
import de.feelix.ocean.client.NettyClient;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A simple example demonstrating how to use the Paralyzed client.
 */
public class SimpleClientExample {

    public static void main(String[] args) {
        try {
            // Default connection parameters
            String host = "localhost";
            int port = 8888;
            
            // Parse command line arguments if provided
            if (args.length > 0) {
                host = args[0];
            }
            
            if (args.length > 1) {
                try {
                    port = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port number: " + args[1]);
                    System.err.println("Using default port: " + port);
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
                
                // Create a packet
                SimpleInPacket packet = new SimpleInPacket(transactionId, input);
                
                // Send the packet and get the response
                CompletableFuture<OutPacket> future = client.sendPacket(packet);
                
                try {
                    // Wait for the response
                    OutPacket response = future.get();
                    
                    // Process the response
                    if (response.isSuccess()) {
                        System.out.println("Response: " + response.getResponse());
                    } else {
                        System.err.println("Error: " + response.getErrorMessage());
                    }
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