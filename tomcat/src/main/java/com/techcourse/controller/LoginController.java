package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        final Cookie cookie = request.getCookie();
        final Session session = SessionManager.find(cookie.getValue());
        if (validateSession(session)) {
            return HttpResponse.redirection("/index.html", TEXT_HTML_CHARSET_UTF_8);
        }

        final String url = request.getRequestLine().getUrl();
        final URL resource = getClass().getClassLoader().getResource("static" + url + ".html");
        validateNullResource(resource);
        final String responseBody;
        try {
            responseBody = Files.readString(Paths.get(resource.toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return HttpResponse.ok(responseBody, TEXT_HTML_CHARSET_UTF_8);
    }

    private boolean validateSession(final Session session) {
        return session != null && session.getAttribute("user") != null;
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        String body = request.getBody();
        final String[] queryStringParts = body.split("&");
        final String account = queryStringParts[0].split("=")[1];
        final String password = queryStringParts[1].split("=")[1];

        final User user = getUserByAccount(account);
        if (isLoginFailed(user, password)) {
            try {
                final URL resource = getClass().getClassLoader().getResource("static" + "/401" + ".html");
                validateNullResource(resource);
                final String responseBody = Files.readString(Paths.get(resource.toURI()));
                return HttpResponse.unAuthentication(responseBody, TEXT_HTML_CHARSET_UTF_8);
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        log.info("user: {}", user);
        final String url = request.getRequestLine().getUrl();
        final URL resource = getClass().getClassLoader().getResource("static" + url + ".html");
        validateNullResource(resource);
        final Cookie newCookie = HttpCookie.createCookie();
        Session session = new Session(newCookie.getValue());
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

    private void validateNullResource(final URL resource) {
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 resource 입니다.");
        }
    }

}
