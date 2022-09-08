package org.apache.catalina.connector;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.catalina.connector.ConnectorProperties.ConnectorThread;

public class ConnectorCustomizeFactory {

    private ConnectorProperties connectorProperties;

    public ConnectorCustomizeFactory(final ConnectorProperties connectorProperties) {
        this.connectorProperties = connectorProperties;
    }

    public Connector customize() {
        ConnectorThread connectorThread = connectorProperties.getConnectorThread();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(connectorThread.getCorePoolSize(),
                connectorThread.getMaxPoolSize(),
                connectorThread.getKeepAliveTime(),
                connectorThread.getTimeUnit(),
                new LinkedBlockingDeque<>(connectorThread.getMaxQueueSize()));

        return new Connector(connectorProperties.getPort(), connectorProperties.getAcceptCount(), threadPoolExecutor);
    }
}
