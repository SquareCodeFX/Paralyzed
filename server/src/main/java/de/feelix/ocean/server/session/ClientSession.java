package de.feelix.ocean.server.session;

import io.netty.channel.Channel;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a client session.
 * This class tracks information about a connected client.
 */
public class ClientSession {
    private final String sessionId;
    private final Channel channel;
    private final Instant creationTime;
    private Instant lastActivityTime;
    private final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();
    
    /**
     * Creates a new ClientSession with the specified channel.
     *
     * @param channel The client's channel
     */
    public ClientSession(Channel channel) {
        this.sessionId = UUID.randomUUID().toString();
        this.channel = channel;
        this.creationTime = Instant.now();
        this.lastActivityTime = Instant.now();
    }
    
    /**
     * Gets the session ID.
     *
     * @return The session ID
     */
    public String getSessionId() {
        return sessionId;
    }
    
    /**
     * Gets the client's channel.
     *
     * @return The channel
     */
    public Channel getChannel() {
        return channel;
    }
    
    /**
     * Gets the creation time of the session.
     *
     * @return The creation time
     */
    public Instant getCreationTime() {
        return creationTime;
    }
    
    /**
     * Gets the last activity time of the session.
     *
     * @return The last activity time
     */
    public Instant getLastActivityTime() {
        return lastActivityTime;
    }
    
    /**
     * Updates the last activity time to the current time.
     */
    public void updateLastActivityTime() {
        this.lastActivityTime = Instant.now();
    }
    
    /**
     * Sets an attribute in the session.
     *
     * @param key The attribute key
     * @param value The attribute value
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    /**
     * Gets an attribute from the session.
     *
     * @param key The attribute key
     * @return The attribute value, or null if not found
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    /**
     * Removes an attribute from the session.
     *
     * @param key The attribute key
     * @return The previous value associated with the key, or null if there was no mapping
     */
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }
    
    /**
     * Checks if the session has an attribute.
     *
     * @param key The attribute key
     * @return true if the session has the attribute, false otherwise
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
    
    /**
     * Clears all attributes from the session.
     */
    public void clearAttributes() {
        attributes.clear();
    }
}