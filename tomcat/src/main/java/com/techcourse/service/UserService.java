package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Arrays;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.general.Cookies;
import org.apache.coyote.http11.handler.controllerResponse.ControllerResponse;
import org.apache.coyote.http11.handler.controllerResponse.JsonResponse;
import org.apache.coyote.http11.handler.controllerResponse.StaticFileResponse;
import org.apache.coyote.http11.httpRequest.CookieParser;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final SessionManager sessionManager;

    public UserService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public ControllerResponse loginPage(HttpRequest httpRequest) {
        String sessionId = findSessionId(httpRequest);
        if (sessionId == null) {
            JsonResponse response = new JsonResponse(HttpStatus.FOUND, "");
            response.addHeader("Location", "/login.html");
            return response;
        }
        JsonResponse response = new JsonResponse(HttpStatus.FOUND, "");
        response.addHeader("Location", "/index.html");
        return response;
    }

    private String findSessionId(HttpRequest httpRequest) {
        Cookies cookies = CookieParser.parseFromHttpRequest(httpRequest);
        if (cookies.isEmpty()) {
            return null;
        }
        return cookies.get("JSESSIONID");
    }

    public ControllerResponse login(HttpRequest httpRequest) {
        String account = httpRequest.getBodyValueOf("account");
        String password = httpRequest.getBodyValueOf("password");
        if (account != null && password != null) {
            return handleLoginResult(account, password);
        }
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    private ControllerResponse handleLoginResult(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
            .orElse(null);
        if (user == null || !user.isPasswordValid(password)) {
            return new StaticFileResponse(HttpStatus.UNAUTHORIZED, "401");
        }
        logger.info(user.toString());
        Session session = buildSessionOfUser(user);
        JsonResponse response = new JsonResponse(HttpStatus.FOUND, "");
        response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
        response.addHeader("Location", "/index.html");
        return response;
    }

    private Session buildSessionOfUser(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        sessionManager.add(session);
        return session;
    }

    public ControllerResponse registerPage() {
        return new StaticFileResponse(HttpStatus.OK, "register");
    }

    public ControllerResponse register(HttpRequest httpRequest) {
        String account = httpRequest.getBodyValueOf("account");
        String email = httpRequest.getBodyValueOf("email");
        String password = httpRequest.getBodyValueOf("password");
        User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
        Session session = buildSessionOfUser(newUser);
        JsonResponse response = new JsonResponse(HttpStatus.FOUND, "");
        response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
        response.addHeader("Location", "/index.html");
        return response;
    }
}
