package com.techcourse.controller;

import static org.apache.coyote.http11.response.HttpResponseHeaderNames.SET_COOKIE;

import java.util.Map;
import java.util.UUID;

import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String ASSIGN_OPERATOR = "=";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (request.getPath().equals("/login")) {
            if (isAlreadyLogin(request.getRequestHeader())) {
                response.redirect(request.getVersion(), "/index.html");
            }
            response.redirect(request.getVersion(), "/login.html");
        }
    }

    private boolean isAlreadyLogin(Map<String, String> requestHeaders) {
        if (requestHeaders.containsKey("Cookie")) {
            String sessionId = requestHeaders.get("Cookie").split("=")[1];
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.findSession(sessionId);
            return session != null;
        }
        return false;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> requestBody = request.getRequestBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        if (!user.checkPassword(password)) {
            response.redirect(request.getVersion(), "/401.html");
            return;
        }
        response.redirect(request.getVersion(), "/index.html");

        String sessionId = UUID.randomUUID().toString();
        createSession(user, createCookie(JSESSIONID, sessionId));
        response.addHeader(SET_COOKIE.getHeaderName(), String.format("%s=%s", JSESSIONID, sessionId));
    }

    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie();
        cookie.setCookie(name, value);
        return cookie;
    }

    private void createSession(User user, Cookie cookie) {
        Map<String, String> cookies = cookie.getCookies();
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = new Session(cookies.get(JSESSIONID));
        sessionManager.add(session);
        session.setAttribute("user", user);
    }
}
