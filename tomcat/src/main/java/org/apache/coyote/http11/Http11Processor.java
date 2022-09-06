package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.application.AuthService;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.exception.InvalidLoginFormatException;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.InvalidSignUpFormatException;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.exception.InvalidHttpRequestFormatException;
import org.apache.coyote.exception.QueryStringFormatException;
import org.apache.coyote.exception.ResourceNotFoundException;
import org.apache.coyote.support.HttpRequestParser;
import org.apache.coyote.support.ResourcesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LANDING_PAGE_URL = "/";
    private static final String STATIC_PATH = "static";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String REGISTER_PATH = "/register";
    private static final String LOGIN_PATH = "/login";
    private static final String SIGN_UP_REDIRECT_PATH = "/index.html";
    private static final String LOGIN_REDIRECT_PATH = "/index.html";
    private static final String BAD_REQUEST_PATH = "/400.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String NOT_FOUND_PATH = "/404.html";
    private static final String INTERNAL_SERVER_ERROR_PATH = "/500.html";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";

    private final Socket connection;
    private final AuthService authService = AuthService.instance();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            HttpResponse response = respond(httpRequest);

            outputStream.write(response.toResponseFormat().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse respond(final HttpRequest httpRequest) {
        try {
            return access(httpRequest);
        } catch (InvalidLoginFormatException | InvalidPasswordException | MemberNotFoundException e) {
            log.error(e.getMessage(), e);
            return toFoundResponse(httpRequest, UNAUTHORIZED_PATH);
        } catch (ResourceNotFoundException e) {
            log.error(e.getMessage(), e);
            return toFoundResponse(httpRequest, NOT_FOUND_PATH);
        } catch (InvalidHttpRequestFormatException | QueryStringFormatException | InvalidSignUpFormatException |
                 DuplicateAccountException e) {
            log.error(e.getMessage(), e);
            return toFoundResponse(httpRequest, BAD_REQUEST_PATH);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return toFoundResponse(httpRequest, INTERNAL_SERVER_ERROR_PATH);
        }
    }

    private HttpResponse access(final HttpRequest httpRequest) throws IOException {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        if (httpMethod == HttpMethod.GET) {
            return accessGetMethod(httpRequest);
        }
        if (httpMethod == HttpMethod.POST) {
            return accessPostMethod(httpRequest);
        }
        return toFoundResponse(httpRequest, NOT_FOUND_PATH);
    }

    private HttpResponse accessGetMethod(final HttpRequest httpRequest)
            throws IOException {
        String url = httpRequest.getUrl();
        if (url.equals(LANDING_PAGE_URL)) {
            return toOkResponse(httpRequest, ContentType.TEXT_HTML, DEFAULT_RESPONSE_BODY);
        }
        ContentType contentType = HttpRequestParser.parseContentType(url);
        String resourceUrl = addExtension(url);
        String responseBody = ResourcesUtil.readResource(STATIC_PATH + resourceUrl);
        return toOkResponse(httpRequest, contentType, responseBody);
    }

    private String addExtension(String url) {
        String extension = HttpRequestParser.parseExtension(url);
        if (extension.isBlank()) {
            url = url + DEFAULT_EXTENSION;
        }
        return url;
    }

    private HttpResponse accessPostMethod(final HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        String requestBody = httpRequest.getBody();
        if (url.equals(REGISTER_PATH)) {
            authService.signUp(requestBody);
            return toFoundResponse(httpRequest, SIGN_UP_REDIRECT_PATH);
        }
        if (url.equals(LOGIN_PATH)) {
            authService.login(requestBody);
            return toFoundResponse(httpRequest, LOGIN_REDIRECT_PATH);
        }
        return toFoundResponse(httpRequest, NOT_FOUND_PATH);
    }

    private HttpResponse toOkResponse(final HttpRequest httpRequest, final ContentType contentType,
                                      final String responseBody) {
        Map<String, String> responseHeaders = new HashMap<>();
        setSessionId(httpRequest, responseHeaders);
        responseHeaders.put("Content-Type", contentType.getValue() + ";charset=utf-8");
        if (!responseBody.isBlank()) {
            responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes().length));
        }
        return new HttpResponse(HttpStatus.OK, responseHeaders, responseBody);
    }

    private HttpResponse toFoundResponse(final HttpRequest httpRequest, final String location) {
        Map<String, String> responseHeaders = new HashMap<>();
        setSessionId(httpRequest, responseHeaders);
        responseHeaders.put("Location", location);
        return new HttpResponse(HttpStatus.FOUND, responseHeaders, "");
    }

    private void setSessionId(final HttpRequest httpRequest, final Map<String, String> responseHeaders) {
        HttpCookie httpCookie = httpRequest.getCookie();
        if (!httpCookie.has("JSESSIONID")) {
            responseHeaders.put("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
        }
    }
}
