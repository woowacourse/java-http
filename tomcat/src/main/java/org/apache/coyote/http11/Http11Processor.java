package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.ClientRequestException;
import org.apache.coyote.http11.exception.ErrorResponseHandler;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            handleRequest(outputStream, inputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequest(OutputStream outputStream, InputStream inputStream) throws IOException {
        try {
            HttpResponse response = new HttpResponse(outputStream);
            ErrorResponseHandler.getInstance().setResponse(response);
            RequestReader reader = new RequestReader(inputStream);
            HttpRequest request = reader.getHttpRequest();
            HttpRequestHandler handler = new HttpRequestHandler();
            handler.handle(request, response);
        } catch (ClientRequestException e) {
            e.handleErrorResponse();
        }
    }
}
