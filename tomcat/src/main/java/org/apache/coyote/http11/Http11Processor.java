package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.handler.HttpRequestHandlerContainer;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final HttpRequestHandlerContainer handlerContainer = new HttpRequestHandlerContainer();

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
            final HttpRequest request = HttpRequest.fromInputStream(inputStream);
            if (request == null) {
                return;
            }
            final HttpResponse response = processResponse(request);

            response.write(outputStream);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse processResponse(HttpRequest request) {
        String url = request.url();
        HttpRequestHandler httpRequestHandler = handlerContainer.getHandler(url);
        return httpRequestHandler.handle(request);
    }
}
