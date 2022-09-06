package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.HttpRequestHandler;
import nextstep.jwp.handler.LoginRequestHandler;
import nextstep.jwp.handler.RegisterRequestHandler;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.Location;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.HttpRequestBody;
import nextstep.jwp.http.request.HttpRequestHeaders;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.util.ResourcesUtil;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final HttpVersion HTTP_VERSION = HttpVersion.HTTP_1_1;

    private final Socket connection;
    private final HttpRequestHandler loginRequestHandler = new LoginRequestHandler(HTTP_VERSION);
    private final HttpRequestHandler registerRequestHandler = new RegisterRequestHandler(HTTP_VERSION);

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

            String requestLine = reader.readLine();

            HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.from(parseHttpRequestHeaders(reader));
            HttpRequestBody httpRequestBody = parseHttpRequestBody(reader, httpRequestHeaders);

            HttpRequest httpRequest = HttpRequest.of(requestLine, httpRequestHeaders, httpRequestBody);

            final HttpResponse response = handleHttpRequest(httpRequest);

            outputStream.write(response.httpResponse());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> parseHttpRequestHeaders(final BufferedReader reader) throws IOException {
        List<String> httpRequestHeaders = new ArrayList<>();
        String line = reader.readLine();
        if (line == null) {
            return httpRequestHeaders;
        }
        while (!"".equals(line)) {
            httpRequestHeaders.add(line);
            line = reader.readLine();
        }
        return httpRequestHeaders;
    }

    private HttpRequestBody parseHttpRequestBody(final BufferedReader reader, final HttpRequestHeaders httpRequestHeaders)
            throws IOException {
        if (httpRequestHeaders.isContainContentLength()) {
            int contentLength = httpRequestHeaders.contentLength();
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return HttpRequestBody.from(new String(buffer));
        }
        return HttpRequestBody.empty();
    }

    private HttpResponse handleHttpRequest(final HttpRequest httpRequest) {
        if (isLoginRequest(httpRequest.getPath())) {
            return loginRequestHandler.handleHttpRequest(httpRequest);
        }
        if (isRegisterRequest(httpRequest.getPath())) {
            return registerRequestHandler.handleHttpRequest(httpRequest);
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
        return new HttpResponse(HTTP_VERSION, HttpStatus.OK, contentType, Location.empty(), HttpCookie.empty(), responseBody);
    }

    private boolean isLoginRequest(final String path) {
        return path.equals("/login");
    }

    private boolean isRegisterRequest(final String path) {
        return path.equals("/register");
    }
}
