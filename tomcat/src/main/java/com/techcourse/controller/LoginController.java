package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.servlet.http.Cookie;
import org.apache.catalina.servlet.http.Session;
import org.apache.catalina.servlet.http.SessionManager;
import org.apache.catalina.servlet.http.request.HttpRequest;
import org.apache.catalina.servlet.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    public String getLoginPage(HttpRequest request, HttpResponse response) {
        if (request.hasCookie()) {
            Cookie cookie = request.getCookie();
            SessionManager sessionManager = SessionManager.getInstance();
            if (sessionManager.hasSession(cookie.getValue())) {
                response.sendRedirect("/index.html");
            }
        }
        return "/login";
    }

    public void login(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(user -> login(request, response, user), () -> response.sendRedirect("/401.html"));
    }

    private void login(HttpRequest request, HttpResponse response, User user) {
        if (user.checkPassword(request.getParameter("password"))) {
            log.info("user : {}", user);
            Session session = request.getSession();
            session.setAttribute("user", user);
            response.addCookie(new Cookie("JSESSIONID", session.getId()));
            response.sendRedirect("/index.html");
            return;
        }
        response.sendRedirect("/401.html");
    }
}
