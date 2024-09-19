package org.apache.catalina.server;

public abstract class ApplicationConfig {

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;

    private Integer port = DEFAULT_PORT;
    private Integer acceptCount = DEFAULT_ACCEPT_COUNT;
    private Integer maxThreads = DEFAULT_MAX_THREADS;

    public Integer getPort() {
        return port;
    }

    public Integer getAcceptCount() {
        return acceptCount;
    }

    public Integer getMaxThreads() {
        return maxThreads;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setAcceptCount(Integer acceptCount) {
        this.acceptCount = acceptCount;
    }

    public void setMaxThreads(Integer maxThreads) {
        this.maxThreads = maxThreads;
    }
}
