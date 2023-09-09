package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.ResourceReader;
import org.apache.coyote.http11.exception.NotCorrectPasswordException;
import org.apache.coyote.http11.exception.NotFoundAccountException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.apache.coyote.http11.response.ResponsePage.INDEX_PAGE;
import static org.apache.coyote.http11.response.ResponsePage.LOGIN_PAGE;

public class LoginController extends AbstractController {
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String STATIC_PATH = "static";
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        login(request, response);
        response.foundResponse(INDEX_PAGE.gerResource());
    }

    private void login(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> requestParam = request.getRequestBody().getParam();
        final User user = InMemoryUserRepository.findByAccount(requestParam.get(ACCOUNT))
                .orElseThrow(NotFoundAccountException::new);
        if (!user.checkPassword(requestParam.get(PASSWORD))) {
            throw new NotCorrectPasswordException();
        }
        addCookie(request, response);
        log.info(user.toString());
    }

    private void addCookie(final HttpRequest request, final HttpResponse response) {
        final HttpCookie cookie = request.getCookies();
        if (!cookie.checkSessionId()) {
            final UUID sessionId = UUID.randomUUID();
            createSession(sessionId.toString());
            response.addCookie(cookie.makeCookieValue(sessionId));
        }
    }

    private static void createSession(final String sessionId) {
        final Session session = new Session(sessionId);
        final SessionManager sessionManager = new SessionManager();
        sessionManager.add(session);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isLoggedIn(request)) {
            response.foundResponse(INDEX_PAGE.gerResource());
            return;
        }
        final String responseBody = ResourceReader.readResource(STATIC_PATH + LOGIN_PAGE.gerResource());
        response.getResponse(request, responseBody);
    }

    private boolean isLoggedIn(final HttpRequest request) {
        return findSession(request).isPresent();
    }

    private Optional<Session> findSession(final HttpRequest request) {
        SessionManager sessionManager = new SessionManager();
        String jSessionId = request.getCookies().getSessionId();

        return sessionManager.findSession(jSessionId);
    }
}
