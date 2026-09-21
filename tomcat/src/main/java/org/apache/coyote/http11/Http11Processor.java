package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_RESOURCE_PATH = "/";
    private static final String DEFAULT_VALUE = "Hello world!";

    private final Socket connection;
    private final Map<String, RequestHandler> handlers;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.handlers = Map.of("/login", new LoginRequestHandler());
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

            final String line = getHttpRequestLine(bufferedReader);
            HttpRequestParser httpRequestParser = new HttpRequestParser();
            HttpRequest httpRequest = httpRequestParser.parseRequestLine(line);
            HttpResponse httpResponse = handleRequest(httpRequest);

            final var responseBody = createResponseBody(httpResponse.path());
            final String contentType = getContentType(httpResponse.path());

            final var response = String.join("\r\n",
                    "HTTP/1.1 " + httpResponse.httpStatus().getMessage() + " ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    new String(responseBody));

            log.info("http method: {}, path: {}, http status: {}", httpRequest.httpMethod(), httpResponse.path(), httpResponse.httpStatus().getMessage());
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getHttpRequestLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null) {
            throw new IllegalArgumentException("HTTP Request Line은 null일 수 없습니다.");
        }
        return line;
    }

    private HttpResponse handleRequest(HttpRequest request) {
        final RequestHandler requestHandler = handlers.get(request.path());

        if (requestHandler == null) {
            return new HttpResponse(request.path(), HttpStatus.OK);
        }

        if (request.params().isEmpty()) {
            return new HttpResponse(request.path(), HttpStatus.OK);
        }

        return requestHandler.handle(request.params());
    }

    private byte[] createResponseBody(String requestTarget) throws IOException, URISyntaxException {
        String resourcePath = getResourcePath(requestTarget);

        if (requestTarget.equals(DEFAULT_RESOURCE_PATH)) {
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

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
