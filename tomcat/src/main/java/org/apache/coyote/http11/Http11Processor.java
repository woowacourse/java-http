package org.apache.coyote.http11;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.HttpRequestFactory;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.presentation.Controller;
import nextstep.jwp.handler.RequestHandler;
import org.apache.coyote.Processor;
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
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            final var bufferedOutputStream = new BufferedOutputStream(connection.getOutputStream())) {

            HttpRequest httpRequest = HttpRequestFactory.create(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();

            Controller controller = RequestHandler.findController(httpRequest.getRequestUri());
            controller.service(httpRequest, httpResponse);

            writeResponse(bufferedOutputStream, httpResponse.toString());
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
