package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.servlet.DispatcherServlet;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        LOGGER.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
            final var outputStream = connection.getOutputStream();
            final var bufferedInputStream = new BufferedInputStream(inputStream);
            final var inputStreamReader = new InputStreamReader(bufferedInputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            final HttpRequest httpRequest = parseRequest(bufferedReader);
            final HttpResponse httpResponse = new DispatcherServlet().service(httpRequest);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseRequest(final BufferedReader bufferedReader) {
        return HttpRequest.from(bufferedReader);
    }
}
