package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http.handler.RequestHandler;
import org.apache.coyote.http.request.HttpRequestParser;
import org.apache.coyote.http.response.HttpResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;
    private final RequestHandler requestHandler;
    private final HttpRequestParser requestParser;
    private final HttpResponseWriter responseWriter;

    public Http11Processor(final Socket connection, final SessionManager sessionManager) {
        this.connection = connection;
        this.requestHandler = new RequestHandler();
        this.requestParser = new HttpRequestParser(sessionManager);
        this.responseWriter = new HttpResponseWriter();
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

            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final var request = requestParser.parse(bufferedReader);
            final var response = requestHandler.handleRequest(request);
            responseWriter.write(response, outputStream);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
