package org.apache.coyote.http11;

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
    private static final String DEFAULT_RESOURCE_PATH = "/";
    private static final String DEFAULT_VALUE = "Hello world!";

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

            String responsePath = httpRequest.path();
            if (httpResponse.headers().containsKey("Location")){
                responsePath = httpResponse.headers().get("Location");
            }

            final var responseBody = createResponseBody(responsePath);
            httpResponse.setBody(responseBody);
            final HttpResponseWriter httpResponseWriter = new HttpResponseWriter();
            String response = httpResponseWriter.write(httpRequest, httpResponse);

            log.info("mehtod: {} , path: {}, http status: {}",
                    httpRequest.httpMethod(), responsePath, httpResponse.status().getMessage());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(HttpRequest request) throws Exception{
        final HttpResponse response = new HttpResponse();
        final Optional<Controller> controller = requestMapping.getController(request);

        if (controller.isEmpty()) {
            response.setStatus(HttpStatus.NOT_FOUND);
            return response;
        }

        controller.get().service(request, response);
        return response;
    }

    private byte[] createResponseBody(String responsePath) throws IOException{
        String resourcePath = getResourcePath(responsePath);

        if (responsePath.equals(DEFAULT_RESOURCE_PATH)) {
            return DEFAULT_VALUE.getBytes();
        }

        final URL resource = Objects.requireNonNull(
                getClass().getClassLoader().getResource(resourcePath));
        final Path path = new File(resource.getFile()).toPath();
        return Files.readAllBytes(path);
    }

    private String getResourcePath(String requestTarget) {
        String resourcePath = "static" + requestTarget;
        if (!requestTarget.contains(".")) {
            resourcePath = resourcePath.concat(".html");
        }
        return resourcePath;
    }
}
