package de.feelix.ocean.api.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.feelix.ocean.api.packet.Packet;
import de.feelix.ocean.api.packet.PacketType;
import de.feelix.ocean.api.validation.ValidationException;

import java.io.IOException;

/**
 * Serializer for packets using Gson.
 * This class handles the serialization and deserialization of packets.
 */
public class PacketSerializer {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(PacketType.class, new PacketTypeAdapter())
        .create();

    /**
     * Serializes a packet to JSON.
     * The packet type is included in the JSON to allow for proper deserialization.
     * The packet is validated before serialization.
     *
     * @param packet The packet to serialize
     * @return The JSON string
     * @throws ValidationException if the packet is invalid
     */
    public static String serialize(Packet packet) {
        // Validate the packet before serialization
        packet.validate();

        JsonObject json = new JsonObject();
        json.addProperty("type", packet.getType().getIdentifier());
        json.add("data", GSON.toJsonTree(packet));
        return GSON.toJson(json);
    }

    /**
     * Deserializes a JSON string to a packet.
     * The packet type is extracted from the JSON to determine the class to instantiate.
     *
     * @param json The JSON string
     * @return The deserialized packet, or null if the packet type is not registered
     * @throws IllegalArgumentException if the packet type is unknown
     */
    public static Packet deserialize(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        String typeIdentifier = jsonObject.get("type").getAsString();
        JsonElement data = jsonObject.get("data");

        Class<? extends Packet> packetClass = PacketRegistry.getPacketClassByIdentifier(typeIdentifier);
        if (packetClass == null) {
            throw new IllegalArgumentException("Unknown packet type: " + typeIdentifier);
        }

        return GSON.fromJson(data, packetClass);
    }

    /**
     * TypeAdapter for PacketType to handle serialization and deserialization.
     */
    private static class PacketTypeAdapter extends TypeAdapter<PacketType> {
        @Override
        public void write(JsonWriter out, PacketType value) throws IOException {
            out.value(value.getIdentifier());
        }

        @Override
        public PacketType read(JsonReader in) throws IOException {
            String identifier = in.nextString();
            PacketType type = PacketType.fromIdentifier(identifier);
            if (type == null) {
                throw new IOException("Unknown packet type: " + identifier);
            }
            return type;
        }
    }
}
