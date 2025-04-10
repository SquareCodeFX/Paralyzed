# Paralyzed

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-11%2B-orange.svg)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
[![Netty](https://img.shields.io/badge/netty-4.1.100-green.svg)](https://netty.io/)
[![Gson](https://img.shields.io/badge/gson-2.10.1-yellow.svg)](https://github.com/google/gson)

Paralyzed is a high-performance, scalable client-server communication framework built on Netty. It provides a robust packet-based communication system with type safety, validation, and efficient serialization.

## Features

- **Modular Architecture**: Separate API, client, and server modules for clean dependency management
- **Type-Safe Packet System**: Enumeration-based packet types with validation
- **Efficient Serialization**: JSON-based serialization using Gson for maximum compatibility
- **Session Management**: Robust client session tracking with timeout handling
- **Flexible Packet Handling**: Registry-based packet handler system for easy extensibility
- **Builder Pattern**: Fluent API for creating packets
- **Comprehensive Validation**: Built-in validation for all packets
- **Error Handling**: Detailed error reporting and exception handling
- **Logging**: Comprehensive logging throughout the codebase

## Modules

### API Module

The API module contains the core interfaces and classes for packet definitions, serialization, and validation. This module is shared between the client and server.

Key components:
- `Packet` interface: Base interface for all packets
- `PacketType` enum: Type-safe packet type definitions
- `PacketSerializer`: JSON serialization/deserialization for packets
- `PacketRegistry`: Registry for mapping packet types to classes
- `Validatable` interface: Interface for objects that can be validated

### Server Module

The server module provides a Netty-based server implementation for receiving and processing packets.

Key components:
- `NettyServer`: Main server implementation
- `PacketHandler`: Interface for handling incoming packets
- `SessionManager`: Manages client sessions
- `PacketHandlerRegistry`: Registry for packet type handlers

### Client Module

The client module provides a Netty-based client implementation for sending packets and receiving responses.

Key components:
- `NettyClient`: Main client implementation
- `ClientPacketHandler`: Handles responses from the server
- `ClientApplication`: Example client application

## Getting Started

### Prerequisites

- Java 11 or higher
- Gradle 7.0 or higher

### Building the Project

```bash
./gradlew build
```

### Running the Server

```bash
./gradlew server:run
```

Or with custom port:

```bash
./gradlew server:run --args="8888"
```

### Running the Client

```bash
./gradlew client:run
```

Or with custom host and port:

```bash
./gradlew client:run --args="localhost 8888"
```

### Running the Examples

The project includes several examples demonstrating how to use the Paralyzed framework:

#### Simple Server Example

```bash
./gradlew examples:runServer
```

#### Simple Client Example

```bash
./gradlew examples:runClient
```

#### Custom Packet Example

```bash
./gradlew examples:runCustomPacket
```

These examples demonstrate basic usage, client-server communication, and how to create custom packet types and handlers.

## Usage Examples

### Creating a Custom Packet

```java
public class CustomInPacket implements InPacket {
    private final String transactionId;
    private final String customData;

    public CustomInPacket(String transactionId, String customData) {
        this.transactionId = transactionId;
        this.customData = customData;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    public String getCustomData() {
        return customData;
    }

    @Override
    public PacketType getType() {
        return PacketType.CUSTOM_IN_PACKET; // Add this to PacketType enum
    }

    @Override
    public void validate() throws ValidationException {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new ValidationException("Transaction ID cannot be null or empty");
        }
        if (customData == null) {
            throw new ValidationException("Custom data cannot be null");
        }
    }
}
```

### Registering a Custom Packet Type

```java
// In PacketType enum
public enum PacketType {
    // Existing types...
    CUSTOM_IN_PACKET("CUSTOM_IN_PACKET"),
    // Other types...
}

// In PacketRegistry
static {
    // Register built-in packet types
    register(PacketType.OUT_PACKET, OutPacket.class);
    register(PacketType.SIMPLE_IN_PACKET, SimpleInPacket.class);
    register(PacketType.CUSTOM_IN_PACKET, CustomInPacket.class);
}
```

### Creating a Custom Packet Handler

```java
public class CustomInPacketHandler implements PacketTypeHandler<CustomInPacket> {
    @Override
    public OutPacket handle(CustomInPacket packet) {
        try {
            // Validate the packet
            packet.validate();

            // Process the custom data
            String customData = packet.getCustomData();
            String response = "Processed: " + customData;

            // Return a success response
            return OutPacket.Builder.success(packet.getTransactionId(), response);
        } catch (Exception e) {
            // Return an error response
            return OutPacket.Builder.error(packet.getTransactionId(), "Error: " + e.getMessage());
        }
    }
}
```

### Registering a Custom Packet Handler

```java
// In your server initialization code
PacketHandler packetHandler = new SimplePacketHandler();
packetHandler.registerHandler(PacketType.CUSTOM_IN_PACKET, new CustomInPacketHandler());
```

### Sending a Packet from the Client

```java
// Create a client
NettyClient client = new NettyClient("localhost", 8888);
client.connect();

// Create a packet
String transactionId = TransactionIdGenerator.generateId();
CustomInPacket packet = new CustomInPacket(transactionId, "Hello, server!");

// Send the packet and get the response
CompletableFuture<OutPacket> future = client.sendPacket(packet);
OutPacket response = future.get();

// Process the response
if (response.isSuccess()) {
    System.out.println("Response: " + response.getResponse());
} else {
    System.err.println("Error: " + response.getErrorMessage());
}

// Shutdown the client
client.shutdown();
```

## Advanced Configuration

### Server Configuration

The server can be configured using the `ServerConfig` class:

```java
ServerConfig config = new ServerConfig()
    .setPort(8888)
    .setBossThreads(1)
    .setWorkerThreads(4)
    .setSessionTimeout(Duration.ofMinutes(30))
    .setEnableSsl(true)
    .setSslCertPath("/path/to/cert.pem")
    .setSslKeyPath("/path/to/key.pem");

PacketHandler packetHandler = new SimplePacketHandler();
SessionManager sessionManager = new SessionManager(config);
NettyServer server = new NettyServer(config.getPort(), packetHandler, sessionManager);
server.start();
```

### Client Configuration

The client can be configured with various options:

```java
// Create a client with custom options
NettyClient client = new NettyClient("localhost", 8888);
client.setConnectTimeout(Duration.ofSeconds(5));
client.setRequestTimeout(Duration.ofSeconds(10));
client.setAutoReconnect(true);
client.setMaxReconnectAttempts(3);
client.connect();
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Netty](https://netty.io/) - The network application framework
- [Gson](https://github.com/google/gson) - JSON serialization/deserialization library
