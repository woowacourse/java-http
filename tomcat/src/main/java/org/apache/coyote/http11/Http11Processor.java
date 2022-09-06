package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestGenerator;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final ServletContainer SERVLET_CONTAINER = ServletContainer.init();

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
        try (
            final var inputStream = connection.getInputStream();
            final var outputStream = connection.getOutputStream();
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            final HttpRequest httpRequest = HttpRequestGenerator.createHttpRequest(bufferedReader);
            final HttpResponse httpResponse = SERVLET_CONTAINER.service(httpRequest);
            final String responseMessage = httpResponse.toMessage();

            outputStream.write(responseMessage.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
