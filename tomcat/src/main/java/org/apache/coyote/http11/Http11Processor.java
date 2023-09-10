package org.apache.coyote.http11;

import static org.apache.coyote.request.Method.GET;
import static org.apache.coyote.request.Method.POST;
import static org.apache.coyote.response.Status.FOUND;
import static org.apache.coyote.response.Status.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.response.Status.NOT_FOUND;
import static org.apache.coyote.response.Status.OK;
import static org.apache.coyote.response.Status.UNAUTHORIZED;
import static org.apache.coyote.utils.Constant.BASE_PATH;
import static org.apache.coyote.utils.Constant.EMPTY;
import static org.apache.coyote.utils.Constant.LINE_SEPARATOR;
import static org.apache.coyote.utils.Parser.parseFormData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.service.UserService;
import org.apache.coyote.Cookies;
import org.apache.coyote.Processor;
import org.apache.coyote.exception.PageNotFoundException;
import org.apache.coyote.exception.UnauthorizedException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.Method;
import org.apache.coyote.request.RequestHeader;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponse.Builder;
import org.apache.coyote.response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final UserService userService = new UserService();

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest request = readRequest(inputStream);
            final HttpResponse response = handleRequest(request);

            outputStream.write(response.stringify().getBytes());
            outputStream.flush();

        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readRequest(final InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final HttpRequest request = readRequestHeader(bufferedReader);
        final String contentLength = request.getHeader(RequestHeader.CONTENT_LENGTH);
        if (contentLength != null) {
            final String requestBody = readRequestBody(bufferedReader, Integer.parseInt(contentLength));
            request.setBody(requestBody);
        }
        return request;
    }

    private HttpRequest readRequestHeader(final BufferedReader bufferedReader) {
        final StringBuilder input = new StringBuilder();
        try {
            for (String s = bufferedReader.readLine();
                 !EMPTY.equals(s);
                 s = bufferedReader.readLine()) {
                input.append(s)
                        .append(LINE_SEPARATOR);
            }
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }

        return HttpRequest.from(input.toString());
    }

    private String readRequestBody(final BufferedReader bufferedReader, final int contentLength) {
        final char[] buffer = new char[contentLength];
        try {
            bufferedReader.read(buffer, 0, contentLength);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
        return new String(buffer);
    }

    private HttpResponse handleRequest(final HttpRequest request) {
        try {
            return handleRequestWithMethod(request);
        } catch (final PageNotFoundException e) {
            log.error(e.getMessage());
            return handleResponseWithException(request, NOT_FOUND);
        } catch (final UnauthorizedException e) {
            log.error(e.getMessage());
            return handleResponseWithException(request, UNAUTHORIZED);
        } catch (final Exception e) {
            log.error(e.getMessage());
            return handleResponseWithException(request, INTERNAL_SERVER_ERROR);
        }
    }

    private HttpResponse handleRequestWithMethod(final HttpRequest request) {
        final Method method = request.getMethod();
        if (method == GET) {
            return handleGetRequest(request);
        }
        if (method == POST) {
            return handlePostRequest(request);
        }
        throw new PageNotFoundException(request.getPath());
    }

    private HttpResponse handleGetRequest(final HttpRequest request) {
        final Builder getResponseBuilder = HttpResponse.builder(
                request.getProtocol(),
                request.getContentType(),
                OK
        );

        final String path = request.getPath();
        if (path.equals(BASE_PATH)) {
            return getResponseBuilder.body("Hello world!").build();
        }
        if (path.equals("/favicon.ico")) {
            return getResponseBuilder.body("Icon Not Exist!").build();
        }
        if (path.equals("/login") && request.isCookieExist(SESSION_COOKIE_NAME)) {
            return handleAuthResponse(request, request.getCookie(SESSION_COOKIE_NAME));
        }

        try {
            final URL resource = convertPathToUrl(path);
            return getResponseBuilder.body(resource).build();
        } catch (final Exception e) {
            throw new PageNotFoundException(path);
        }
    }

    private HttpResponse handleAuthResponse(final HttpRequest request, final String uuid) {
        final Builder authResponseBuilder = HttpResponse.builder(
                request.getProtocol(),
                request.getContentType(),
                FOUND
        ).redirectUri("/index.html");

        if (!request.isCookieExist(SESSION_COOKIE_NAME)) {
            final Cookies cookies = Cookies.create(SESSION_COOKIE_NAME, uuid);
            authResponseBuilder.cookies(cookies);
        }

        return authResponseBuilder.build();
    }

    private URL convertPathToUrl(String path) {
        if (!path.contains(".")) {
            path += ".html";
        }
        return getClass().getClassLoader().getResource("static" + path);
    }

    private HttpResponse handlePostRequest(final HttpRequest request) throws UnauthorizedException {
        final String path = request.getPath();
        if (path.equals("/login")) {
            return logIn(request);
        }
        if (path.equals("/register")) {
            return register(request);
        }
        throw new PageNotFoundException(request.getPath());
    }

    private HttpResponse logIn(final HttpRequest request) throws UnauthorizedException {
        final Map<String, String> bodyFields = parseFormData(request.getBody());
        final String uuid = userService.logIn(bodyFields);
        return handleAuthResponse(request, uuid);
    }

    private HttpResponse register(final HttpRequest request) {
        final Map<String, String> bodyFields = parseFormData(request.getBody());
        final String uuid = userService.register(bodyFields);
        return handleAuthResponse(request, uuid);
    }

    private HttpResponse handleResponseWithException(final HttpRequest request, final Status status) {
        final URL resource = convertPathToUrl(BASE_PATH + status.getCode());
        return HttpResponse.builder(
                        request.getProtocol(),
                        request.getContentType(),
                        status
                )
                .body(resource)
                .build();
    }
}
