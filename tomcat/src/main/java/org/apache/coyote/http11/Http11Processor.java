package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.enums.HttpMethod;
import org.apache.coyote.http11.enums.HttpStatus;
import org.apache.coyote.http11.handler.LoginPageHandler;
import org.apache.coyote.http11.handler.LoginRequestHandler;
import org.apache.coyote.http11.handler.RegisterRequestHandler;
import org.apache.coyote.http11.handler.RequestHandler;
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
    private final Map<Route, RequestHandler> handlers;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.handlers = Map.of(
                new Route(HttpMethod.GET, "/login"), new LoginPageHandler(),
                new Route(HttpMethod.POST, "/login"), new LoginRequestHandler(),
                new Route(HttpMethod.POST, "/register"), new RegisterRequestHandler()
        );
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

            var header = new StringBuilder();
            addResponseHeaderInfo(httpResponse, header);

            final var responseBody = createResponseBody(httpResponse.path());
            final String contentType = getContentType(httpResponse.path());

            var response = new StringBuilder();
            String responseLine = httpRequest.version() + " " + httpResponse.httpStatus().getMessage() + " ";
            header.append(String.join("\r\n",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.length + " "));
            String body = new String(responseBody);

            response.append(String.join("\r\n", responseLine, header.toString() + "\r\n", body));

            log.info("mehtod: {} , path: {}, http status: {}",
                    httpRequest.httpMethod(), httpResponse.path(), httpResponse.httpStatus().getMessage());
            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(HttpRequest request) {
        final RequestHandler requestHandler = handlers.get(new Route(request.httpMethod(), request.path()));

        if (requestHandler == null) {
            return new HttpResponse(request.path(), HttpStatus.OK, new HashMap<>());
        }

        return requestHandler.handle(request);
    }

    private void addResponseHeaderInfo(HttpResponse httpResponse, StringBuilder header) {
        if (httpResponse.headers().containsKey("cookie")) {
            header.append("Set-Cookie: JSESSIONID=")
                    .append(httpResponse.headers().get("cookie"))
                    .append("\r\n");
        }

        if (httpResponse.headers().containsKey("Location")) {
            header.append("Location: ")
                    .append(httpResponse.headers().get("Location"))
                    .append("\r\n");
        }
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
