package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.StaticResource;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String LOGIN_PAGE = "/login.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String USER = "user";
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (isLoggedIn(request)) {
            response.sendRedirect(INDEX_PAGE);
            return;
        }
        if (request.hasQueryString()) {
            login(request.getQueryParameter("account"),
                request.getQueryParameter("password"), response);
            return;
        }

        StaticResource.serve(response, LOGIN_PAGE);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> formData = request.getFormData();
        login(formData.get("account"), formData.get("password"), response);
    }

    private boolean isLoggedIn(HttpRequest request) throws IOException {
        String sessionId = request.getCookie(JSESSIONID);
        Session session = SessionManager.getInstance().findSession(sessionId);

        return session != null && session.getAttribute(USER) != null;
    }

    private void login(String account, String password, HttpResponse response)
        throws IOException {
        User user = InMemoryUserRepository.findByAccount(account)
            .orElse(null);

        if (user == null || !user.isMatchPassword(password)) {
            log.info("login failed: {}", account);
            StaticResource.serve(response, UNAUTHORIZED_PAGE, HttpStatus.UNAUTHORIZED);
            return;
        }

        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(USER, user);
        SessionManager.getInstance().add(session);

        log.info("login success: {}, sessionId: {}", user, session.getId());
        response.sendRedirect(INDEX_PAGE);
        response.addCookie(JSESSIONID, session.getId());
    }
}
