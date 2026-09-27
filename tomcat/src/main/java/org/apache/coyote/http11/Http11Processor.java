package org.apache.coyote.http11;

import org.apache.catalina.StaticResourceHandler;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.enums.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
//    private static final String DEFAULT_RESOURCE_PATH = "/";
//    private static final String DEFAULT_VALUE = "Hello world!";

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            final HttpRequestParser httpRequestParser = new HttpRequestParser();
            HttpRequest httpRequest = httpRequestParser.parse(bufferedReader);
            HttpResponse httpResponse = handleRequest(httpRequest);

            final HttpResponseWriter httpResponseWriter = new HttpResponseWriter();
            String response = httpResponseWriter.write(httpRequest, httpResponse);

            log.info("mehtod: {} , path: {}, http status: {}",
                    httpRequest.httpMethod(), httpRequest.path(), httpResponse.status().getMessage());
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(HttpRequest request) throws Exception{
        final HttpResponse response = new HttpResponse();
        final Optional<Controller> controller = requestMapping.getController(request);

        if (controller.isPresent()){
            controller.get().service(request, response);
        }

        StaticResourceHandler staticResourceHandler = new StaticResourceHandler();
        staticResourceHandler.handle(request, response);
        return response;
    }
}
