package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.catalina.container.Container;
import org.apache.catalina.ExceptionHandler;
import org.apache.catalina.HandlerMapping;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.http.HttpVersion;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.HttpRequestReader;
import org.apache.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Container container;

    public Http11Processor(final Socket connection, final Container container) {
        this.connection = connection;
        this.container = container;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = HttpRequestReader.readHttpRequest(bufferedReader);
            final HttpResponse response = process(httpRequest);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse process(final HttpRequest httpRequest) throws Exception {
        final HandlerMapping handlerMapping = container.getHandlerMapping();
        final Controller controller = handlerMapping.getHandler(httpRequest);
        final HttpResponse httpResponse = HttpResponse.builder().version(HttpVersion.HTTP_1_1).build();

        try {
            controller.service(httpRequest, httpResponse);
        } catch (Exception e) {
            final ExceptionHandler exceptionHandler = container.getExceptionHandler();
            exceptionHandler.getHandler(e).service(httpRequest, httpResponse);
        }

        return httpResponse;
    }
}
