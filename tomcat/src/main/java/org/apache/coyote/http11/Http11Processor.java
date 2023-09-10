package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.servlet.Servlet;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger logger = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Servlet servlet;

    public Http11Processor(final Socket connection, final Servlet servlet) {
        this.connection = connection;
        this.servlet = servlet;
    }

    @Override
    public void run() {
        logger.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
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
            final HttpRequest httpRequest = HttpRequestFactory.createHttpRequest(bufferedReader);
            final HttpResponse httpResponse = HttpResponse.createBasicResponse();
            servlet.service(httpRequest, httpResponse);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
