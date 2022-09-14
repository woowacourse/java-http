package org.apache.coyote.http11;

import http.request.HttpRequest;
import http.response.HttpResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.container.Container;
import org.apache.container.exception.ContainerNotInitializedException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Container container;

    public Http11Processor(final Socket connection, final Container container) {
        this.connection = connection;
        this.container = container;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            HttpResponse response = container.respond(httpRequest);

            outputStream.write(response.toResponseFormat().getBytes());
            outputStream.flush();
        } catch (IOException | ContainerNotInitializedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
