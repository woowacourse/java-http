package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.manager.Session;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final SessionManager sessionManager = SessionManager.getInstance();

    protected void doGet(HttpRequest request, HttpResponse response) {
        HttpCookie cookie = request.getCookie();
        String sessionId = cookie.get("JSESSIONID");

        if (sessionId != null) {
            Session session = sessionManager.findSession(sessionId);
            if (session != null && session.getAttribute("user") != null) {
                response.redirect("/index.html");
                return;
            }
        }

        response.getStaticResource("/login.html");
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            Map<String, String> bodys = getBody(request.getBody());
            User user = InMemoryUserRepository.findByAccount(bodys.get("account"))
                    .orElseThrow();
            if (!user.checkPassword(bodys.get("password"))) {
                throw new RuntimeException();
            }
            log.debug("user: {}", user);

            Session session = new Session();
            session.setAttribute("user", user);
            sessionManager.add(session);

            response.setStatusCode(StatusCode.FOUND);
            response.addHeader("Location", "/index.html");
            response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
        } catch (Exception e) {
            response.redirect("/401.html");
        }
    }

    private Map<String, String> getBody(String body) {
        Map<String, String> bodys = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyAndValue = pair.split("=");
            bodys.put(keyAndValue[0], keyAndValue[1]);
        }
        return bodys;
    }
}
