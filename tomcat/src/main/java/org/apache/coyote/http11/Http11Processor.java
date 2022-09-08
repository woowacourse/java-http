package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.UncheckedServletException;
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

            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final HttpResponse httpResponse = generateHttpResponse(httpRequest);

            final String response = httpResponse.toResponseMessage();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse generateHttpResponse(HttpRequest httpRequest) {
        try {
            final Controller controller = HandlerMapping.findController(httpRequest);
            return controller.service(httpRequest);
        } catch (Exception exception) {
            final ExceptionHandler errorHandler = ErrorMapping.findErrorHandler(exception);
            return errorHandler.handle();
        }
    }
}
