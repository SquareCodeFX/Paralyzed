package de.feelix.ocean.server.config;

import java.time.Duration;

/**
 * Configuration for the server.
 * This class holds various settings for the server.
 */
public class ServerConfig {
    private int port;
    private int bossThreads;
    private int workerThreads;
    private Duration sessionTimeout;
    private boolean enableSsl;
    private String sslCertPath;
    private String sslKeyPath;
    
    /**
     * Creates a new ServerConfig with default values.
     */
    public ServerConfig() {
        this.port = 8888;
        this.bossThreads = 1;
        this.workerThreads = 0; // 0 means use Netty's default (2 * number of CPU cores)
        this.sessionTimeout = Duration.ofMinutes(30);
        this.enableSsl = false;
        this.sslCertPath = null;
        this.sslKeyPath = null;
    }
    
    /**
     * Gets the port to listen on.
     *
     * @return The port
     */
    public int getPort() {
        return port;
    }
    
    /**
     * Sets the port to listen on.
     *
     * @param port The port
     * @return This config
     */
    public ServerConfig setPort(int port) {
        this.port = port;
        return this;
    }
    
    /**
     * Gets the number of boss threads.
     *
     * @return The number of boss threads
     */
    public int getBossThreads() {
        return bossThreads;
    }
    
    /**
     * Sets the number of boss threads.
     *
     * @param bossThreads The number of boss threads
     * @return This config
     */
    public ServerConfig setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
        return this;
    }
    
    /**
     * Gets the number of worker threads.
     *
     * @return The number of worker threads
     */
    public int getWorkerThreads() {
        return workerThreads;
    }
    
    /**
     * Sets the number of worker threads.
     *
     * @param workerThreads The number of worker threads
     * @return This config
     */
    public ServerConfig setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
        return this;
    }
    
    /**
     * Gets the session timeout.
     *
     * @return The session timeout
     */
    public Duration getSessionTimeout() {
        return sessionTimeout;
    }
    
    /**
     * Sets the session timeout.
     *
     * @param sessionTimeout The session timeout
     * @return This config
     */
    public ServerConfig setSessionTimeout(Duration sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        return this;
    }
    
    /**
     * Checks if SSL is enabled.
     *
     * @return true if SSL is enabled, false otherwise
     */
    public boolean isEnableSsl() {
        return enableSsl;
    }
    
    /**
     * Sets whether SSL is enabled.
     *
     * @param enableSsl true to enable SSL, false to disable
     * @return This config
     */
    public ServerConfig setEnableSsl(boolean enableSsl) {
        this.enableSsl = enableSsl;
        return this;
    }
    
    /**
     * Gets the path to the SSL certificate.
     *
     * @return The path to the SSL certificate
     */
    public String getSslCertPath() {
        return sslCertPath;
    }
    
    /**
     * Sets the path to the SSL certificate.
     *
     * @param sslCertPath The path to the SSL certificate
     * @return This config
     */
    public ServerConfig setSslCertPath(String sslCertPath) {
        this.sslCertPath = sslCertPath;
        return this;
    }
    
    /**
     * Gets the path to the SSL key.
     *
     * @return The path to the SSL key
     */
    public String getSslKeyPath() {
        return sslKeyPath;
    }
    
    /**
     * Sets the path to the SSL key.
     *
     * @param sslKeyPath The path to the SSL key
     * @return This config
     */
    public ServerConfig setSslKeyPath(String sslKeyPath) {
        this.sslKeyPath = sslKeyPath;
        return this;
    }
}