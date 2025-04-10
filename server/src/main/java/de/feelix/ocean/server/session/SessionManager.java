package de.feelix.ocean.server.session;

import de.feelix.ocean.server.config.ServerConfig;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Manager for client sessions.
 * This class tracks all active client sessions and provides methods for managing them.
 */
public class SessionManager {
    private static final Logger LOGGER = Logger.getLogger(SessionManager.class.getName());
    private static final AttributeKey<String> SESSION_ID_KEY = AttributeKey.valueOf("sessionId");

    private final Map<String, ClientSession> sessions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Duration sessionTimeout;

    /**
     * Creates a new SessionManager with default settings and starts the session cleanup task.
     */
    public SessionManager() {
        this(new ServerConfig());
    }

    /**
     * Creates a new SessionManager with the specified configuration and starts the session cleanup task.
     *
     * @param config The server configuration
     */
    public SessionManager(ServerConfig config) {
        this.sessionTimeout = config.getSessionTimeout();

        // Schedule a task to clean up expired sessions
        scheduler.scheduleAtFixedRate(this::cleanupExpiredSessions, 1, 1, TimeUnit.MINUTES);

        LOGGER.info("Session manager initialized with timeout: " + sessionTimeout);
    }

    /**
     * Creates a new session for a channel.
     *
     * @param channel The client's channel
     * @return The new session
     */
    public ClientSession createSession(Channel channel) {
        ClientSession session = new ClientSession(channel);
        sessions.put(session.getSessionId(), session);

        // Store the session ID in the channel's attributes
        channel.attr(SESSION_ID_KEY).set(session.getSessionId());

        return session;
    }

    /**
     * Gets a session by its ID.
     *
     * @param sessionId The session ID
     * @return The session, or null if not found
     */
    public ClientSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * Gets a session for a channel.
     *
     * @param channel The client's channel
     * @return The session, or null if not found
     */
    public ClientSession getSession(Channel channel) {
        String sessionId = channel.attr(SESSION_ID_KEY).get();
        return sessionId != null ? sessions.get(sessionId) : null;
    }

    /**
     * Removes a session.
     *
     * @param sessionId The session ID
     * @return The removed session, or null if not found
     */
    public ClientSession removeSession(String sessionId) {
        return sessions.remove(sessionId);
    }

    /**
     * Removes a session for a channel.
     *
     * @param channel The client's channel
     * @return The removed session, or null if not found
     */
    public ClientSession removeSession(Channel channel) {
        String sessionId = channel.attr(SESSION_ID_KEY).get();
        return sessionId != null ? sessions.remove(sessionId) : null;
    }

    /**
     * Gets all active sessions.
     *
     * @return A list of all active sessions
     */
    public List<ClientSession> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }

    /**
     * Gets the number of active sessions.
     *
     * @return The number of active sessions
     */
    public int getSessionCount() {
        return sessions.size();
    }

    /**
     * Cleans up expired sessions.
     */
    private void cleanupExpiredSessions() {
        Instant now = Instant.now();
        List<String> expiredSessionIds = new ArrayList<>();

        // Find expired sessions
        for (ClientSession session : sessions.values()) {
            if (Duration.between(session.getLastActivityTime(), now).compareTo(sessionTimeout) > 0) {
                expiredSessionIds.add(session.getSessionId());
            }
        }

        // Remove expired sessions
        for (String sessionId : expiredSessionIds) {
            ClientSession session = sessions.remove(sessionId);
            if (session != null && session.getChannel().isActive()) {
                session.getChannel().close();
            }
        }
    }

    /**
     * Shuts down the session manager.
     */
    public void shutdown() {
        scheduler.shutdown();

        // Close all active channels
        for (ClientSession session : sessions.values()) {
            if (session.getChannel().isActive()) {
                session.getChannel().close();
            }
        }

        sessions.clear();
    }
}
