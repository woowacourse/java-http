package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import org.apache.catalina.ResourceResolver;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.cookie.HttpCookie;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.session.Session;
import org.apache.coyote.http.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class); //로깅 클래스 변경

    private final ResourceResolver resourceResolver = new ResourceResolver();

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws URISyntaxException, IOException {
        final Optional<Cookie> cookie = request.getCookieByHeaders();
        if (cookie.isEmpty()) {
            return renderLoginPage(request);
        }

        final Session session = SessionManager.find(cookie.get().getValue());
        if (validateSession(session)) {
            return HttpResponse.redirection("index.html", TEXT_HTML_CHARSET_UTF_8);
        }

        return renderLoginPage(request);
    }

    private boolean validateSession(final Session session) {
        return session != null && session.isValid("user");
    }

    private HttpResponse renderLoginPage(final HttpRequest request) throws IOException, URISyntaxException {
        final URL resource = resourceResolver.resolver(request.getRequestLine().getUrl());
        final String responseBody = Files.readString(Paths.get(resource.toURI()));
        return HttpResponse.ok(responseBody, TEXT_HTML_CHARSET_UTF_8);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws URISyntaxException, IOException {
        final String account = request.getBody().get("account");
        final String password = request.getBody().get("password");

        final User user = getUserByAccount(account);
        if (isLoginFailed(user, password)) {
            final URL resource = resourceResolver.resolver(request.getRequestLine().getUrl());
            final String responseBody = Files.readString(Paths.get(resource.toURI()));

            return HttpResponse.unAuthentication(responseBody, TEXT_HTML_CHARSET_UTF_8); //리다이렉트
        }

        log.info("user: {}", user);
        final Cookie newCookie = HttpCookie.createCookie();
        final Session session = new Session(newCookie.getValue());
        session.setAttribute("user", user);
        SessionManager.add(session);
        return HttpResponse.redirectionWithCookie("/index.html", TEXT_HTML_CHARSET_UTF_8, newCookie);
    }

    private User getUserByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElse(null);
    }

    private boolean isLoginFailed(final User user, final String password) {
        return user == null || isNotMatchPassword(user, password);
    }

    private boolean isNotMatchPassword(final User user, final String password) {
        return !user.checkPassword(password);
    }
}
