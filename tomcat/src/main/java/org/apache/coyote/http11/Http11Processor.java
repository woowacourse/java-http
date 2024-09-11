package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Dispatcher;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestMessageReader;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Dispatcher dispatcher;

    public Http11Processor(final Socket connection, Dispatcher dispatcher) {
        this.connection = connection;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequest request = HttpRequestMessageReader.read(inputStream);
            HttpResponse response = HttpResponse.ok();
            handleRequest(request, response);
            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequest(HttpRequest request, HttpResponse response) {
        try {
            dispatcher.dispatch(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.setStatus(HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void writeResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.getAsBytes());
        outputStream.flush();
    }
}
