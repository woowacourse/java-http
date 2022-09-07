package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.exception.LoginFailedException;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.ResourceNotFoundException;
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

            final HttpResponse httpResponse = getHttpResponse(bufferedReader);

            final String response = httpResponse.toResponseMessage();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getHttpResponse(BufferedReader bufferedReader) {
        final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        try {
            final HandlerMapping handler = HandlerMapping.findHandler(httpRequest);
            final HttpResponse httpResponse = handler.doService(httpRequest);
            return httpResponse;
        } catch (ResourceNotFoundException exception) {
            return ErrorResponse.resourceNotFound();
        } catch (LoginFailedException exception) {
            return ErrorResponse.loginFailed();
        }
    }
}
