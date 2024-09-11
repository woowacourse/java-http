package com.techcourse.controller;

import com.techcourse.controller.dto.LoginRequest;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.util.StaticResourceManager;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final ResponseFile loginPage = StaticResourceManager.read("/login.html");
    private static final String SESSION_KEY_USER = "user";

    private final SessionManager sessionManager;

    public LoginController() {
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    public String matchedPath() {
        return "/login";
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        sessionManager.getSession(request.getSessionId())
                .map(s -> s.getAttribute(SESSION_KEY_USER))
                .ifPresentOrElse(
                        user -> response.sendRedirect("index.html"),
                        () -> response.setStatus(HttpStatusCode.OK).setBody(loginPage)
                );
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        LoginRequest loginRequest = LoginRequest.of(request.getBody());
        String userAccount = loginRequest.account();
        String userPassword = loginRequest.password();
        log.info("로그인 요청 -  id: {}, password: {}", userAccount, userPassword);

        InMemoryUserRepository.findByAccount(userAccount)
                .filter(user -> user.checkPassword(userPassword))
                .ifPresentOrElse(
                        user -> doLogin(request, response, user),
                        () -> response.sendRedirect("/401.html")
                );
    }

    private void doLogin(HttpRequest request, HttpResponse response, User user) {
        Session session = sessionManager.getSession(request.getSessionId())
                .orElseGet(this::registerNewSession);
        session.setAttribute(SESSION_KEY_USER, user);
        response.sendRedirect("/index.html")
                .setCookie("JSESSIONID", session.getId());
    }

    private Session registerNewSession() {
        Session newSession = new Session(UUID.randomUUID().toString());
        sessionManager.add(newSession);
        return newSession;
    }
}
