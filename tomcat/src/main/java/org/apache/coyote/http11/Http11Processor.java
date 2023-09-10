package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.servlet.ServletContainer;
import org.apache.servlet.SimpleServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            HttpRequest request = new HttpRequest(inputStream);
            HttpResponse response = new HttpResponse();
            SimpleServlet servlet = ServletContainer.find(request);
            servlet.service(request, response);
            response.addToOutputStream(outputStream);
            outputStream.flush();
        } catch (IOException | RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }
}
