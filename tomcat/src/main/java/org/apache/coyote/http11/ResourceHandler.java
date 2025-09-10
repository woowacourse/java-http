package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
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

    private static void setContentType(final HttpRequest request, final HttpResponse response) {
        if (request.getResourcePath().endsWith(".html")) {
            response.setContentType(TEXT_HTML_CHARSET_UTF_8);
            return;
        }
        if (request.getResourcePath().endsWith(".css")) {
            response.setContentType(TEXT_CSS_CHARSET_UTF_8);
            return;
        }
        if (request.getResourcePath().endsWith(".js")) {
            response.setContentType(APPLICATION_JAVASCRIPT_CHARSET_UTF_8);
            return;
        }
        response.setContentType(DEFAULT_CONTENT_TYPE);
    }

    private void setBody(final HttpRequest request, final HttpResponse response) {
        if (Objects.equals(request.getResourcePath(), "/")) {
            response.setBodyAndContentLength("Hello world!");
            return;
        }
        if (request.hasQueryParameter()) {
            if (Objects.equals(request.getResourcePath(), "/login")) {
                redirectLogin(request, response);
                return;
            }
        }
        if (!request.hasQueryParameter()) {
            response.setStatusCode(StatusCode.OK);
            response.setBodyAndContentLength(getContent(request.getResourcePath()));
        }
    }

    private void redirectLogin(final HttpRequest request, final HttpResponse response) {
        String account = request.getQueryParameter("account");
        String password = request.getQueryParameter("password");

        if (account == null || account.isBlank()) {
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            response.setBodyAndContentLength(getContent("/401.html"));
            return;
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : {}", user);
            response.setStatusCode(StatusCode.FOUND);
            response.setLocation("/index.html");
            return;
        }
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        response.setBodyAndContentLength(getContent("/401.html"));
    }

    private String getContent(final String resourcePath) {
        try (final var inputStream = getClass().getClassLoader().getResourceAsStream(getWholeResourcePath(resourcePath))) {
            if (inputStream == null) {
                return "Not found: " + resourcePath;
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
