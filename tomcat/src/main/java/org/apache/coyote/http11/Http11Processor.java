package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.handler.ExceptionHandler;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String response = processRequest(bufferedReader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String processRequest(BufferedReader bufferedReader) {
        try {
            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final HttpResponse httpResponse = generateHttpResponse(httpRequest);

            return httpResponse.toResponseMessage();
        } catch (Exception exception) {
            final ExceptionHandler errorHandler = ErrorMapping.findErrorHandler(exception);
            final HttpResponse httpResponse = errorHandler.handle();

            return httpResponse.toResponseMessage();
        }
    }

    private HttpResponse generateHttpResponse(HttpRequest httpRequest) {
        final Controller controller = HandlerMapping.findController(httpRequest);
        return controller.service(httpRequest);
    }
}
