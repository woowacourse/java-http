package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.util.ResourceParser;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.httpRequest.HttpCookie;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        if (httpRequest.hasCookie()) {
            final String cookie = httpRequest.findCookie().get();
            final HttpCookie httpCookie = HttpCookie.parse(cookie);
            final String sessionId = httpCookie.getCookies().get("JSESSIONID");

            if (sessionId != null) {
                final Session session = SessionManager.findSession(sessionId);

                final User user = (User) session.getAttribute("user");
                if (user != null) {
                    return HttpResponse.status(HttpStatus.FOUND)
                            .location(Page.INDEX.getPath())
                            .setCookie(httpCookie);
                }
            }
        }

        return HttpResponse.status(HttpStatus.OK)
                .build(Page.LOGIN.getPath(), ResourceParser.parse(Page.LOGIN.getPath()));
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) throws Exception {
        try {
            final String account = findValueFromParams(httpRequest, "account");
            final String password = findValueFromParams(httpRequest, "password");

            final Optional<User> userOrEmpty = InMemoryUserRepository.findByAccount(account);
            if (userOrEmpty.isPresent()) {
                final User user = userOrEmpty.get();
                log.info("user: {}", user);

                if (!user.checkPassword(password)) {
                    return HttpResponse.status(HttpStatus.UNAUTHORIZED)
                            .build(Page.UNAUTHORIZED.getPath(), ResourceParser.parse(Page.UNAUTHORIZED.getPath()));
                }

                final Session session = Session.create();
                session.setAttribute("user", user);
                SessionManager.add(session);
                final HttpCookie httpCookie = HttpCookie.create(session.getSessionId());

                return HttpResponse.status(HttpStatus.FOUND)
                        .location(Page.INDEX.getPath())
                        .setCookie(httpCookie);
            }

            return HttpResponse.status(HttpStatus.FOUND)
                    .location(Page.LOGIN.getPath());
        } catch (final IllegalArgumentException e) {
            return HttpResponse.status(HttpStatus.BAD_REQUEST)
                    .build(Page.BAD_REQUEST.getPath(), ResourceParser.parse(Page.BAD_REQUEST.getPath()));
        }
    }

    private String findValueFromParams(
            final HttpRequest httpRequest,
            final String name
    ) {
        return httpRequest.findParamsValueFromBody(name)
                .orElseThrow(() -> new IllegalArgumentException("파라미터의 키 값이 존재하지 않습니다: " + name));
    }
}
