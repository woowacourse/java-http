package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.controller.StaticResource;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String SESSION_USER = "user";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (isLoggedIn(request.getCookie())) {
            response.sendRedirect(INDEX_PAGE);
            return;
        }
        StaticResource.serve(LOGIN_PAGE, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final User existUser = InMemoryUserRepository.findByAccount(request.getFormParameter("account"))
                .filter(user -> user.checkPassword(request.getFormParameter("password")))
                .orElse(null);
        if (existUser == null) {
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }
        log.info("user : {}", existUser);
        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(SESSION_USER, existUser);
        SessionManager.getInstance().add(session);
        response.sendRedirect(INDEX_PAGE);
        response.setCookie(HttpCookie.ofJSessionId(session.getId()));
    }

    private boolean isLoggedIn(final HttpCookie cookie) {
        return cookie.getJSessionId()
                .map(SessionManager.getInstance()::findSession)
                .map(session -> session.getAttribute(SESSION_USER))
                .isPresent();
    }
}
