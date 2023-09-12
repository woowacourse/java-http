package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.catalina.startup.Container;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.ResourceHandler;
import org.apache.coyote.http11.handler.ResourceHandlerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestReader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Container container;
    private final Socket connection;

    public Http11Processor(final Container container, final Socket connection) {
        this.container = container;
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
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequest request = HttpRequestReader.read(bufferedReader);
            if (request.isEmpty()) {
                return;
            }
            final HttpResponse response = handleRequest(request);

            HttpResponseWriter.write(outputStream, response);
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(final HttpRequest request) throws IOException {
        HttpResponse response = HttpResponse.create();

        container.handle(request, response);
        if (!response.isEmpty()) {
            return response;
        }

        final ResourceHandler resourceHandler = ResourceHandlerMapper.findHandler(request);
        resourceHandler.service(request, response);
        return response;
    }
}
