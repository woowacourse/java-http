package org.apache.coyote.http11;

import nextstep.jwp.controller.Handler;
import nextstep.jwp.controller.HandlerMapper;
import org.apache.coyote.Processor;
import org.apache.coyote.exception.NotFoundContentTypeException;
import org.apache.coyote.exception.NotFoundFileException;
import org.apache.coyote.exception.NotFoundHttpMethodException;
import org.apache.coyote.exception.UncheckedServletException;
import org.apache.coyote.model.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static org.apache.coyote.model.response.StatusCode.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.model.response.StatusCode.METHOD_NOT_ALLOWED;
import static org.apache.coyote.model.response.StatusCode.NOT_FOUND;

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String httpResponse = getHttpResponse(reader);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final HttpRequest httpRequest) {
        final Handler handler = HandlerMapper.findHandler(httpRequest);
        return handler.getResponse(httpRequest);
    }

    private String getHttpResponse(BufferedReader reader) {
        var httpResponse = "";
        try {
            final var httpRequest = HttpRequest.of(reader);
            httpResponse = createResponse(httpRequest);
        } catch (NotFoundContentTypeException | NotFoundFileException | NotFoundHttpMethodException e) {
            httpResponse = ErrorHandler.getINSTANCE()
                    .getResponse(NOT_FOUND, e.getMessage());
        } catch (UncheckedServletException e) {
            httpResponse = ErrorHandler.getINSTANCE()
                    .getResponse(METHOD_NOT_ALLOWED, e.getMessage());
        } catch (Exception e) {
            httpResponse = ErrorHandler.getINSTANCE()
                    .getResponse(INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return httpResponse;
    }
}
