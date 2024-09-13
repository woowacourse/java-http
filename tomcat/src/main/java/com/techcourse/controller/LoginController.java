package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.manager.Session;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    protected void doGet(HttpRequest request, HttpResponse response) {
        HttpCookie cookie = request.getCookie();

        Optional.ofNullable(cookie.get("JSESSIONID"))
                .map(sessionManager::findSession)
                .filter(session -> session.getAttribute("user") != null)
                .ifPresentOrElse(user -> response.redirect("/index.html"),
                        () -> response.getStaticResource("/login.html"));
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            Map<String, String> bodys = request.parseQueryParameters();

            String account = bodys.get("account");
            String password = bodys.get("password");

            if (account == null || password == null) {
                response.redirect("/login.html");
                return;
            }

            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow();
            if (!user.checkPassword(password)) {
                throw new RuntimeException();
            }

            log.debug("user: {}", user);
            Session session = new Session();
            session.setAttribute("user", user);
            sessionManager.add(session);

            response.setStatusCode(StatusCode.FOUND);
            response.addHeader(HttpHeaders.LOCATION, "/index.html");
            response.addHeader(HttpHeaders.SET_COOKIE, "JSESSIONID=" + session.getId());
        } catch (Exception e) {
            response.redirect("/401.html");
        }
    }
}
