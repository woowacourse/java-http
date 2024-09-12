package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Cookie;
import org.apache.catalina.request.HttpMethod;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public HttpResponse execute(HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        if (httpMethod == HttpMethod.GET) {
            return getLoginPage(httpRequest);
        }
        if (httpMethod == HttpMethod.POST) {
            return login(httpRequest);
        }
        return new HttpResponse(401);
    }

    private HttpResponse login(HttpRequest httpRequest) {
        Map<String, String> params = httpRequest.getBody();
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(params.get("account"));
        if (optionalUser.isEmpty()) {
            return new HttpResponse(302, "/401.html");
        }
        User user = optionalUser.get();
        if (user.checkPassword(params.get("password"))) {
            Session session = new Session();
            session.setAttribute("user", user);
            sessionManager.add(session);
            log.info("{}", user);
            Cookie cookie = new Cookie(Map.of("JSESSIONID", session.getId()));
            return new HttpResponse(302, "/index.html", cookie);
        }
        return new HttpResponse(302, "/401.html");
    }

    public HttpResponse getLoginPage(HttpRequest httpRequest) {
        String sessionId = httpRequest.getSessionId();
        Session session = sessionManager.findSession(sessionId);
        if (session.isPresent() && session.getAttribute("user") != null) {
            return new HttpResponse(302, "/index.html");
        }
        return new HttpResponse(200, httpRequest.getUrl());
    }
}
