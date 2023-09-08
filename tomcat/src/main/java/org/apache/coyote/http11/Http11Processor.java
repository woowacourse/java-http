package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.controller.RequestHandler;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestHandler requestHandler;
    private final HttpRequestParser httpRequestParser;

    public Http11Processor(final Socket connection, final RequestHandler requestHandler) {
        this.connection = connection;
        this.requestHandler = requestHandler;
        this.httpRequestParser = new HttpRequestParser();
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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final HttpRequest httpRequest = httpRequestParser.parseHttpRequest(bufferedReader);
            if (httpRequest == null) {
                return;
            }

            final HttpResponse httpResponse = new HttpResponse();
            requestHandler.handle(httpRequest, httpResponse);

            outputStream.write(httpResponse.serialize().getBytes());
            outputStream.flush();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
