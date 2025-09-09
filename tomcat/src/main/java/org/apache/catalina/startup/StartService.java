package org.apache.catalina.startup;

import org.apache.coyote.connector.Connector;

public class StartService implements Runnable{

    private final Connector connector;

    public StartService(final Connector connector) {
        this.connector = connector;
    }

    @Override
    public void run() {
        connector.start();
    }

    public void stop() {
        connector.stop();
    }
}
