package org.apache.catalina.connector;

import java.util.concurrent.TimeUnit;

public class ConnectorProperties {

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;

    private int port;
    private int acceptCount;
    private ConnectorThread connectorThread;

    public ConnectorProperties(final int port, final int acceptCount,
                               final ConnectorThread connectorThread) {
        this.port = port;
        this.acceptCount = acceptCount;
        this.connectorThread = connectorThread;
    }

    public static ConnectorProperties createDefault() {
        return new ConnectorProperties(8080, 100, ConnectorThread.createDefault());
    }

    public int getPort() {
        return port;
    }

    public int getAcceptCount() {
        return acceptCount;
    }

    public ConnectorThread getConnectorThread() {
        return connectorThread;
    }

    static class ConnectorThread {

        private static final int DEFAULT_CORE_POOL_SIZE = 25;
        private static final int DEFAULT_MAX_POOL_SIZE = 200;
        private static final long DEFAULT_KEEP_ALIVE_TIME = 60L;
        private static final int DEFAULT_MAX_QUEUE_SIZE = 10;

        private int corePoolSize;
        private int maxPoolSize;
        private Long keepAliveTime;
        private TimeUnit timeUnit;
        private int maxQueueSize;

        public ConnectorThread(final int corePoolSize, final int maxPoolSize, final Long keepAliveTime,
                               final TimeUnit timeUnit, final int maxQueueSize) {
            this.corePoolSize = corePoolSize;
            this.maxPoolSize = maxPoolSize;
            this.keepAliveTime = keepAliveTime;
            this.timeUnit = timeUnit;
            this.maxQueueSize = maxQueueSize;
        }

        public static ConnectorThread createDefault() {
            return new ConnectorThread(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME,
                    TimeUnit.SECONDS,
                    DEFAULT_MAX_QUEUE_SIZE);
        }

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public int getMaxPoolSize() {
            return maxPoolSize;
        }

        public Long getKeepAliveTime() {
            return keepAliveTime;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        public int getMaxQueueSize() {
            return maxQueueSize;
        }
    }
}
