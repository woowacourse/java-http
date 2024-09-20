package org.apache.catalina.connector;

import org.apache.coyote.adapter.Adapter;
import org.apache.coyote.http11.Http11Processor;

import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class CatalinaConnectionListener implements ConnectionListener {
    private final Adapter adapter;
    private final ExecutorService executorService;

    public CatalinaConnectionListener(final Adapter adapter, final ExecutorService executorService) {
        this.adapter = adapter;
        this.executorService = executorService;
    }

    @Override
    public void onConnection(final Socket connection) {
        final var processor = new Http11Processor(connection, adapter);
        executorService.submit(processor);
    }
}
