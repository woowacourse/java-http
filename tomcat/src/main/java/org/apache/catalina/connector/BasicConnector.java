package org.apache.catalina.connector;

import java.net.Socket;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicConnector extends Connector {

    private static final Logger log = LoggerFactory.getLogger(BasicConnector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;

    public BasicConnector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT);
    }

    public BasicConnector(final int port, final int acceptCount) {
        super(port, acceptCount);
    }

    @Override
    final void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        var processor = new Http11Processor(connection, new CatalinaAdapter());
        new Thread(processor).start();
    }
}
