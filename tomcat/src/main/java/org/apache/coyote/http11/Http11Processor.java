package org.apache.coyote.http11;

import http.request.HttpRequest;
import http.response.HttpResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.config.Configuration;
import org.apache.catalina.handler.ExceptionHandler;
import org.apache.catalina.handler.RequestHandler;
import org.apache.catalina.handler.RequestMapper;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapper requestMapper;
    private final ExceptionHandler exceptionHandler;

    public Http11Processor(final Socket connection, final Configuration configuration) {
        this.connection = connection;
        this.requestMapper = configuration.getRequestMapper();
        this.exceptionHandler = configuration.getExceptionHandler();
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            HttpResponse response = respond(httpRequest);

            outputStream.write(response.toResponseFormat().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse respond(final HttpRequest httpRequest) {
        try {
            RequestHandler requestHandler = requestMapper.findHandler(httpRequest.getUrl());
            return requestHandler.service(httpRequest);
        } catch (Exception e) {
            return exceptionHandler.handle(e);
        }
    }
}
