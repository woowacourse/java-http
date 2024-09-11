package org.apache.catalina.startup;

import org.apache.catalina.connector.ConnectionListener;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.adapter.Adapter;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Tomcat implements ConnectionListener {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private final Connector connector;
    private final Adapter adapter;

    public Tomcat(final Connector connector, final Adapter adapter) {
        this.connector = connector;
        this.connector.setConnectionListener(this);
        this.adapter = adapter;

    }

    public void start() {
        connector.start();
        try {
            // make the application wait until we press any key.
            System.in.read();
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("web server stop.");
            connector.stop();
        }
    }

    @Override
    public void onConnection(final Socket connection) {
        final var processor = new Http11Processor(connection,adapter);
        new Thread(processor).start();
    }
}
