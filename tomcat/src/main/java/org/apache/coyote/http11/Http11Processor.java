package org.apache.coyote.http11;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Controller indexController = new IndexController();
    private final Controller loginController = new LoginController();
    private final Map<String, Controller> handlerMapping = new HashMap<>();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        init();
        process(connection);
    }

    private void init() {
        handlerMapping.put("/", indexController);
        handlerMapping.put("/login", loginController);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {
            final HttpRequestParser httpRequestParser = new HttpRequestParser();
            final HttpRequest httpRequest = httpRequestParser.parse(inputStream);

            final String requestUri = httpRequest.getRequestUri();

            final String view;
            final HttpResponse httpResponse = new HttpResponse();
            if (requestUri.contains(".")) {
                view = requestUri;
                httpResponse.setStatusCode(HttpStatusCode.OK);
            } else {
                final Controller controller = handlerMapping.get(requestUri);
                view = controller.run(httpRequest, httpResponse);
            }

            final ViewResolver viewResolver = new ViewResolver();
            final String responseBody = viewResolver.read(view);
            httpResponse.setHeader("Content-Type", httpRequest.getContentType());
            httpResponse.setHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
            httpResponse.setResponseBody(responseBody);

            final var responseString = makeResponseString(httpResponse);
            outputStream.write(responseString.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponseString(final HttpResponse httpResponse) {
        return String.join("\r\n",
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
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(joining("\r\n"));
    }
}
