package org.apache.coyote.http11;

import static org.apache.coyote.http11.request.Method.GET;
import static org.apache.coyote.http11.request.Method.POST;
import static org.apache.coyote.http11.response.Status.FOUND;
import static org.apache.coyote.http11.response.Status.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.response.Status.NOT_FOUND;
import static org.apache.coyote.http11.response.Status.OK;
import static org.apache.coyote.http11.response.Status.UNAUTHORIZED;
import static org.apache.coyote.http11.utils.Parser.parseFormData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.service.UserService;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.PageNotFoundException;
import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String LINE_SEPARATOR = "\r\n";
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

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();

        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readRequest(final InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final HttpRequest request = readRequestHeader(bufferedReader);
        final String contentLength = request.getHeader("Content-Length");
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
                 !"".equals(s);
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
            return handleResponseWithException(NOT_FOUND);
        } catch (final UnauthorizedException e) {
            log.error(e.getMessage());
            return handleResponseWithException(UNAUTHORIZED);
        } catch (final Exception e) {
            log.error(e.getMessage());
            return handleResponseWithException(INTERNAL_SERVER_ERROR);
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
        final String path = request.getPath();
        if (path.equals("/")) {
            return new HttpResponse(OK, "Hello world!");
        }
        if (path.equals("/favicon.ico")) {
            return new HttpResponse(OK, "Icon Not Exist!");
        }
        if (path.equals("/login") && request.isCookieExist(SESSION_COOKIE_NAME)) {
            return handleAuthResponse(request, request.getCookie(SESSION_COOKIE_NAME));
        }

        try {
            final URL resource = convertPathToUrl(path);
            final HttpResponse response = new HttpResponse(OK, resource);
            checkContentType(request, response);
            return response;
        } catch (final Exception e) {
            throw new PageNotFoundException(path);
        }
    }

    private HttpResponse handleAuthResponse(final HttpRequest request, final String uuid) {
        final HttpResponse response = new HttpResponse(FOUND);

        response.addHeader("Location", "/index.html");
        if (!request.isCookieExist(SESSION_COOKIE_NAME)) {
            response.addHeader("Set-Cookie", "JSESSIONID=" + uuid);
        }
        return response;
    }

    private URL convertPathToUrl(String path) {
        if (!path.contains(".")) {
            path += ".html";
        }
        return getClass().getClassLoader().getResource("static" + path);
    }

    private void checkContentType(final HttpRequest request, final HttpResponse response) {
        final String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("css")) {
            response.addHeader("Content-Type", "text/css;charset=utf-8 ");
        }
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

    private HttpResponse handleResponseWithException(final Status status) {
        final URL resource = convertPathToUrl("/" + status.getCode());
        return new HttpResponse(status, resource);
    }
}
