package de.feelix.ocean.api.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for generating unique transaction IDs.
 */
public class TransactionIdGenerator {
    private static final AtomicLong COUNTER = new AtomicLong(0);
    
    /**
     * Generates a unique transaction ID based on a counter.
     * This is more efficient than UUID for high-frequency operations.
     *
     * @return A unique transaction ID
     */
    public static String generateId() {
        return String.valueOf(COUNTER.incrementAndGet());
    }
    
    /**
     * Generates a unique transaction ID based on UUID.
     * This is more suitable for distributed systems.
     *
     * @return A unique transaction ID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}