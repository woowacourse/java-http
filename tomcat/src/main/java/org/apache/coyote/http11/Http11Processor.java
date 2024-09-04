package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Dispatcher dispatcher;
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        dispatcher = Dispatcher.getInstance();
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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest httpRequest = HttpRequest.from(bufferedReader.readLine());
            HttpRequestHandler httpRequestHandler = dispatcher.mappedHandler(httpRequest);

            String responseBody = httpRequestHandler.handle(httpRequest);
            HttpResponse httpResponse = HttpResponse.htmlResourceOkResponse(outputStream, responseBody);
            httpResponse.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
