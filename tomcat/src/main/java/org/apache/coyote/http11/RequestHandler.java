package org.apache.coyote.http11;

import static org.apache.coyote.http11.request.HttpMethod.GET;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;

public class RequestHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
    private static final String LOGIN_HTML = "/login.html";
    private static final String LOGIN = "/login";
    private final Request request;

    public RequestHandler(final Request request) {
        this.request = request;
    }

    public Response generateResponse() {
        final String requestPath = request.getLine().getRequestPath();
        if (requestPath.equals("/") && request.getHttpMethod().equals(GET)) {
            return getMainPage();
        }

        if (requestPath.equals(LOGIN) && request.getHttpMethod().equals(GET)) {
            return login();
        }

        return getStaticPateResponse(requestPath);
    }

    private Response getStaticPateResponse(final String requestPath) {
        final String responseBody = readFile(requestPath);
        final HttpVersion httpVersion = request.getHttpVersion();
        final StatusCode statusCode = StatusCode.OK;
        final ContentType contentType = ContentType.findByName(requestPath);
        return Response.of(httpVersion, statusCode, contentType, responseBody);
    }

    private String readFile(final String fileName) {
        try {
            final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            URL url = classLoader.getResource("static" + fileName);
            final File file = new File(url.getFile());
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException | NullPointerException exception) {
            throw new IllegalStateException("파일이 없습니다.");
        }
    }

    private Response getMainPage() {
        final String responseBody = "Hello world!";
        final HttpVersion httpVersion = request.getHttpVersion();
        final StatusCode statusCode = StatusCode.OK;
        final ContentType contentType = ContentType.HTML;
        return Response.of(httpVersion, statusCode, contentType, responseBody);
    }

    public Response login() {
        if (request.hasQueryString()) {
            final Map<String, String> queryString = request.getQueryString();
            final String account = queryString.get("account");
            InMemoryUserRepository.findByAccount(account)
                    .ifPresent(user -> {
                        if (user.checkPassword(queryString.get("password"))) {
                            LOGGER.info("user: {}", user);
                        }
                    });
        }
        return getStaticPateResponse(LOGIN_HTML);
    }
}
