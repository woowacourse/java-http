package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.HttpRequestHandler;
import nextstep.jwp.handler.LoginRequestHandler;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.Location;
import nextstep.jwp.util.ResourcesUtil;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final HttpVersion HTTP_VERSION = HttpVersion.HTTP_1_1;

    private final Socket connection;
    private final HttpRequestHandler loginRequestHandler = new LoginRequestHandler(HTTP_VERSION);

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line = reader.readLine();
            HttpRequest httpRequest = HttpRequest.from(line);

            final HttpResponse response = handleHttpRequest(httpRequest);

            outputStream.write(response.httpResponse());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleHttpRequest(final HttpRequest httpRequest) {
        if (isLoginRequest(httpRequest.getPath())) {
            return loginRequestHandler.handleHttpRequest(httpRequest);
        }
        return createResponseBody(httpRequest);
    }

    private HttpResponse createResponseBody(final HttpRequest httpRequest) {
        if (httpRequest.isRootPath()) {
            return helloResponse();
        }
        String responseBody = ResourcesUtil.readResource(httpRequest.getFilePath(), this.getClass());
        return okResponse(httpRequest.getContentType(), responseBody);
    }

    private HttpResponse helloResponse() {
        var responseBody = "Hello world!";
        return okResponse(ContentType.TEXT_HTML, responseBody);
    }

    private HttpResponse okResponse(final ContentType contentType, final String responseBody) {
        return new HttpResponse(HTTP_VERSION, HttpStatus.OK, contentType, Location.empty(), responseBody);
    }

    private boolean isLoginRequest(final String path) {
        return path.equals("/login");
    }
}
