package de.feelix.ocean.api.packet;

import de.feelix.ocean.api.validation.ValidationException;

/**
 * Class for packets sent from server to client.
 * This is the response packet containing a transaction ID and a response string.
 */
public class OutPacket implements Packet {
    private final String transactionId;
    private final String response;
    private final boolean success;
    private final String errorMessage;

    /**
     * Private constructor used by the Builder.
     */
    private OutPacket(Builder builder) {
        this.transactionId = builder.transactionId;
        this.response = builder.response;
        this.success = builder.success;
        this.errorMessage = builder.errorMessage;
    }

    /**
     * Creates a new OutPacket with the specified transaction ID and response.
     * This is a convenience constructor for backward compatibility.
     *
     * @param transactionId The transaction ID from the corresponding InPacket
     * @param response The response string
     */
    public OutPacket(String transactionId, String response) {
        this.transactionId = transactionId;
        this.response = response;
        this.success = true;
        this.errorMessage = null;
    }

    /**
     * Gets the transaction ID for this packet.
     *
     * @return The transaction ID
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Gets the response string.
     *
     * @return The response string
     */
    public String getResponse() {
        return response;
    }

    /**
     * Checks if the operation was successful.
     *
     * @return true if the operation was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the error message if the operation failed.
     *
     * @return The error message, or null if the operation was successful
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT_PACKET;
    }

    @Override
    public void validate() throws ValidationException {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new ValidationException("Transaction ID cannot be null or empty");
        }
        if (!success && (response != null)) {
            throw new ValidationException("Response should be null for failed operations");
        }
        if (success && (response == null)) {
            throw new ValidationException("Response cannot be null for successful operations");
        }
        if (!success && (errorMessage == null || errorMessage.isEmpty())) {
            throw new ValidationException("Error message cannot be null or empty for failed operations");
        }
    }

    /**
     * Builder for OutPacket.
     */
    public static class Builder {
        private String transactionId;
        private String response;
        private boolean success = true;
        private String errorMessage;

        /**
         * Sets the transaction ID.
         *
         * @param transactionId The transaction ID
         * @return This builder
         */
        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        /**
         * Sets the response string.
         *
         * @param response The response string
         * @return This builder
         */
        public Builder response(String response) {
            this.response = response;
            return this;
        }

        /**
         * Sets the success flag.
         *
         * @param success true if the operation was successful, false otherwise
         * @return This builder
         */
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        /**
         * Sets the error message.
         *
         * @param errorMessage The error message
         * @return This builder
         */
        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        /**
         * Creates a new OutPacket with the configured values.
         *
         * @return The new OutPacket
         */
        public OutPacket build() {
            return new OutPacket(this);
        }

        /**
         * Creates a success response with the specified transaction ID and response.
         *
         * @param transactionId The transaction ID
         * @param response The response string
         * @return The new OutPacket
         */
        public static OutPacket success(String transactionId, String response) {
            return new Builder()
                .transactionId(transactionId)
                .response(response)
                .success(true)
                .build();
        }

        /**
         * Creates an error response with the specified transaction ID and error message.
         *
         * @param transactionId The transaction ID
         * @param errorMessage The error message
         * @return The new OutPacket
         */
        public static OutPacket error(String transactionId, String errorMessage) {
            return new Builder()
                .transactionId(transactionId)
                .success(false)
                .errorMessage(errorMessage)
                .build();
        }
    }
}
