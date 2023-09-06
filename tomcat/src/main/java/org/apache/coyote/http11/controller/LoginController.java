package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.common.header.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.statusLine.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String DIRECTORY_SEPARATOR = "/";
    private static final String INDEX_FILE = "index.html";
    private static final String LOGIN_FILE = "login.html";
    private static final String JSESSIONID = "JSESSIONID";


    public ResponseEntity postLogin(final RequestBody requestBody) {
        final String account = requestBody.search("account");
        final String password = requestBody.search("password");

        return InMemoryUserRepository.findByAccount(account)
                                     .filter(user -> user.checkPassword(password))
                                     .map(this::loginSuccess)
                                     .orElseGet(this::loginFail);
    }


    public ResponseEntity getLogin(final HttpRequest httpRequest) {
        if (httpRequest.hasSessionId()) {
            final String jsessionId = httpRequest.findSessionIdFromRequestHeaders(JSESSIONID);
            final Session session = SessionManager.findSession(jsessionId);
            if (session == null) {
                final String loginPath = DIRECTORY_SEPARATOR + LOGIN_FILE;
                return ResponseEntity.of(HttpStatus.OK, loginPath);
            }

            return loginSuccess((User) session.getAttribute("user"));
        }

        final String loginPath = DIRECTORY_SEPARATOR + LOGIN_FILE;

        return ResponseEntity.of(HttpStatus.OK, loginPath);
    }

    private ResponseEntity loginSuccess(final User findUser) {
        log.info("user: {}", findUser);

        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", findUser);
        SessionManager.add(session);

        final HttpCookie httpCookie = new HttpCookie(Map.of(JSESSIONID, session.getId()));
        final String indexPath = DIRECTORY_SEPARATOR + INDEX_FILE;

        return ResponseEntity.of(HttpStatus.FOUND, httpCookie, indexPath);
    }

    private ResponseEntity loginFail() {
        final String unauthorizedPath = DIRECTORY_SEPARATOR + HttpStatus.UNAUTHORIZED.getResourceName();

        return ResponseEntity.of(HttpStatus.UNAUTHORIZED, unauthorizedPath);
    }
}
