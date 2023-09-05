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
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.response.Http11Response;
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

            final Http11Request request = readRequest(inputStream);
            final Http11Response response = handleRequest(request);

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();

        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Http11Request readRequest(final InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final Http11Request request = readRequestHeader(bufferedReader);
        final String contentLength = request.getHeader("Content-Length");
        if (contentLength != null) {
            final String requestBody = readRequestBody(bufferedReader, Integer.parseInt(contentLength));
            request.setBody(requestBody);
        }
        return request;
    }

    private Http11Request readRequestHeader(final BufferedReader bufferedReader) {
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

        return Http11Request.from(input.toString());
    }

    private String readRequestBody(final BufferedReader bufferedReader, final int contentLength) {
        final char[] buffer = new char[contentLength];
        try {
            bufferedReader.read(buffer, 0, contentLength);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return new String(buffer);
    }

    private Http11Response handleRequest(final Http11Request request) {
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

    private Http11Response handleRequestWithMethod(final Http11Request request) {
        final Method method = request.getMethod();
        if (method == GET) {
            return handleGetRequest(request);
        }
        if (method == POST) {
            return handlePostRequest(request);
        }
        throw new PageNotFoundException(request.getPath());
    }

    private Http11Response handleGetRequest(final Http11Request request) {
        final String path = request.getPath();
        if (path.equals("/")) {
            return new Http11Response(OK, "Hello world!");
        }
        if (path.equals("/favicon.ico")) {
            return new Http11Response(OK, "Icon Not Exist!");
        }
        if (path.equals("/login") && request.isCookieExist(SESSION_COOKIE_NAME)) {
            return handleAuthResponse(request, request.getCookie(SESSION_COOKIE_NAME));
        }

        try {
            final URL resource = convertPathToUrl(path);
            final Http11Response response = new Http11Response(OK, resource);
            checkContentType(request, response);
            return response;
        } catch (final Exception e) {
            throw new PageNotFoundException(path);
        }
    }

    private Http11Response handleAuthResponse(final Http11Request request, final String uuid) {
        final Http11Response response = new Http11Response(FOUND);

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

    private void checkContentType(final Http11Request request, final Http11Response response) {
        final String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("css")) {
            response.addHeader("Content-Type", "text/css;charset=utf-8 ");
        }
    }

    private Http11Response handlePostRequest(final Http11Request request) throws UnauthorizedException {
        final String path = request.getPath();
        if (path.equals("/login")) {
            return logIn(request);
        }
        if (path.equals("/register")) {
            return register(request);
        }
        throw new PageNotFoundException(request.getPath());
    }

    private Http11Response logIn(final Http11Request request) throws UnauthorizedException {
        final Map<String, String> bodyFields = parseFormData(request.getBody());
        final String uuid = userService.logIn(bodyFields);
        return handleAuthResponse(request, uuid);
    }

    private Http11Response register(final Http11Request request) {
        final Map<String, String> bodyFields = parseFormData(request.getBody());
        final String uuid = userService.register(bodyFields);
        return handleAuthResponse(request, uuid);
    }

    private Http11Response handleResponseWithException(final Status status) {
        final URL resource = convertPathToUrl("/" + status.getCode());
        return new Http11Response(status, resource);
    }
}
