package org.apache.coyote.http11;

import static java.util.stream.Collectors.joining;

import handler.Controller;
import handler.RequestHandler;
import handler.RequestHandlerMapping;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String EXTENSION_DELIMITER = ".";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpRequestParser httpRequestParser = new HttpRequestParser();
    private static final RequestHandler requestHandler = new RequestHandlerMapping();
    private static final ViewResolver viewResolver = new ViewResolver();

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {
            final HttpRequest httpRequest = httpRequestParser.parse(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse();

            final String viewName = getViewName(httpRequest, httpResponse);
            final String responseBody = viewResolver.read(viewName);

            httpResponse.setHeader("Content-Type", httpRequest.getContentType());
            httpResponse.setHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
            httpResponse.setResponseBody(responseBody);
            final String response = makeResponseString(httpResponse);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getViewName(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final String requestUri = httpRequest.getRequestUri();

        if (isFileRequest(requestUri)) {
            httpResponse.setStatusCode(HttpStatusCode.OK);
            return requestUri;
        }
        final Controller controller = requestHandler.getHandler(requestUri);
        return controller.run(httpRequest, httpResponse);
    }

    private boolean isFileRequest(final String requestUri) {
        return requestUri.contains(EXTENSION_DELIMITER);
    }

    private String makeResponseString(final HttpResponse httpResponse) {
        return String.join(System.lineSeparator(),
                makeResponseCode(httpResponse),
                makeResponseHeaders(httpResponse),
                "",
                httpResponse.getResponseBody());
    }

    private String makeResponseCode(final HttpResponse httpResponse) {
        final int code = httpResponse.getStatusCode().getCode();
        final String message = httpResponse.getStatusCode().getMessage();
        return "HTTP/1.1 " + code + " " + message + " ";
    }

    private String makeResponseHeaders(final HttpResponse httpResponse) {
        final Map<String, String> headers = httpResponse.getHeaders();
        return headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(joining(System.lineSeparator()));
    }
}
