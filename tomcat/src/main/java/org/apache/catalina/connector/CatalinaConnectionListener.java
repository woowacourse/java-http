package org.apache.catalina.connector;

import org.apache.coyote.adapter.Adapter;
import org.apache.coyote.http11.Http11Processor;

import java.net.Socket;

public class CatalinaConnectionListener implements ConnectionListener {
    private final Adapter adapter;

    public CatalinaConnectionListener(final Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onConnection(final Socket connection) {
        final var processor = new Http11Processor(connection, adapter);
        new Thread(processor).start();
    }
}
