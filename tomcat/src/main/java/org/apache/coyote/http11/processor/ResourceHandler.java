package org.apache.coyote.http11.processor;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.model.Cookie;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;
import org.apache.coyote.http11.model.StatusCode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String STATIC_PREFIX = "static";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String EXTENSION_DELIMITER = ".";

    public static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";
    private static final String TEXT_CSS_CHARSET_UTF_8 = "text/css;charset=utf-8";
    private static final String APPLICATION_JAVASCRIPT_CHARSET_UTF_8 = "application/javascript;charset=utf-8";

    public void execute(HttpRequest request, HttpResponse response) {
        setContentType(request, response);
        setBody(request, response);
    }

    private void setContentType(final HttpRequest request, final HttpResponse response) {
        if (request.getPath().endsWith(".html")) {
            response.setContentType(TEXT_HTML_CHARSET_UTF_8);
            return;
        }
        if (request.getPath().endsWith(".css")) {
            response.setContentType(TEXT_CSS_CHARSET_UTF_8);
            return;
        }
        if (request.getPath().endsWith(".js")) {
            response.setContentType(APPLICATION_JAVASCRIPT_CHARSET_UTF_8);
            return;
        }
        response.setContentType(DEFAULT_CONTENT_TYPE);
    }

    private void setBody(final HttpRequest request, final HttpResponse response) {
        if (Objects.equals(request.getPath(), "/")) {
            response.setStatusCode(StatusCode.OK);
            response.setBodyAndContentLength("Hello world!");
            return;
        }
        if (Objects.equals(request.getPath(), "/login")) {
            final var session = request.getSession();
            if (session != null && session.getAttribute("user") != null) {
                response.setStatusCode(StatusCode.FOUND);
                response.sendRedirect("/index.html");
                return;
            }
        }
        if (request.hasQueryParameter()) {
            if (Objects.equals(request.getPath(), "/login")) {
                redirectLogin(request, response);
                return;
            }
            if (Objects.equals(request.getPath(), "/register")) {
                saveUser(request, response);
                return;
            }
        }
        if (!request.hasQueryParameter()) {
            String body = getContent(request.getPath());
            if (body == null) {
                response.setStatusCode(StatusCode.NOT_FOUND);
                body = getContent("/404.html");
            } else {
                response.setStatusCode(StatusCode.OK);
            }
            response.setBodyAndContentLength(body);
        }
    }

    private void redirectLogin(final HttpRequest request, final HttpResponse response) {
        final var account = request.getQueryParameter("account");
        final var password = request.getQueryParameter("password");

        if (account == null || account.isBlank()) {
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            var body = getContent("/401.html");
            response.setBodyAndContentLength(body);
            return;
        }
        final var user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : {}", user);

            final var session = request.getSession();
            session.setAttribute("user", user.get());
            response.addCookie(Cookie.ofJSessionId(session.getId()));
            response.setStatusCode(StatusCode.FOUND);
            response.sendRedirect("/index.html");
            return;
        }
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        var body = getContent("/401.html");
        response.setBodyAndContentLength(body);
    }

    private void saveUser(HttpRequest request, HttpResponse response) {
        final var account = request.getQueryParameter("account");
        final var password = request.getQueryParameter("password");
        final var email = request.getQueryParameter("email");

        final var user = new User(2L, account, password, email);
        InMemoryUserRepository.save(user);

        log.info("user : {}", user);
        response.setStatusCode(StatusCode.FOUND);
        response.sendRedirect("/index.html");
    }

    private String getContent(final String resourcePath) {
        final var wholeResourcePath = getWholeResourcePath(resourcePath);
        try (final var inputStream = getClass().getClassLoader().getResourceAsStream(wholeResourcePath)) {
            if (inputStream == null) {
                return null;
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getWholeResourcePath(final String resourcePathPart) {
        if (resourcePathPart.contains(EXTENSION_DELIMITER)) {
            return STATIC_PREFIX + resourcePathPart;
        }
        return STATIC_PREFIX + resourcePathPart + DEFAULT_EXTENSION;
    }
}
