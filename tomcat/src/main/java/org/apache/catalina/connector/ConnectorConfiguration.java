package org.apache.catalina.connector;

public class ConnectorConfiguration {

    private final int port;
    private final int acceptCount;
    private final int maxThreads;

    public ConnectorConfiguration(int port, int acceptCount, int maxThreads) {
        this.port = port;
        this.acceptCount = acceptCount;
        this.maxThreads = maxThreads;
    }

    public int getPort() {
        return port;
    }

    public int getAcceptCount() {
        return acceptCount;
    }

    public int getMaxThreads() {
        return maxThreads;
    }
}
