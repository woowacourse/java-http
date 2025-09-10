package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.general.CommonHeaderKeys;
import org.apache.coyote.http11.general.Cookies;
import org.apache.coyote.http11.handler.applicationResponse.ApplicationResponse;
import org.apache.coyote.http11.handler.applicationResponse.JsonResponse;
import org.apache.coyote.http11.handler.applicationResponse.StaticFileResponse;
import org.apache.coyote.http11.general.CookieParser;
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

    public ApplicationResponse loginPage(HttpRequest httpRequest) {
        String sessionId = findSessionId(httpRequest);
        if (sessionId == null || !isValidSession(sessionId)) {
            JsonResponse response = new JsonResponse(HttpStatus.FOUND, "");
            response.addHeader(CommonHeaderKeys.LOCATION.getKey(), "/login.html");
            return response;
        }
        JsonResponse response = new JsonResponse(HttpStatus.FOUND, "");
        response.addHeader(CommonHeaderKeys.LOCATION.getKey(), "/index.html");
        return response;
    }

    private String findSessionId(HttpRequest httpRequest) {
        Cookies cookies = CookieParser.parseFromHttpRequest(httpRequest);
        if (cookies.isEmpty()) {
            return null;
        }
        return cookies.get("JSESSIONID");
    }

    private boolean isValidSession(String sessionId) {
        HttpSession session = sessionManager.findSession(sessionId);
        User user = (User) session.getAttribute("user");
        return user != null;
    }

    public ApplicationResponse login(HttpRequest httpRequest) {
        String account = httpRequest.getBodyValueOf("account");
        String password = httpRequest.getBodyValueOf("password");
        if (account != null && password != null) {
            return handleLoginResult(account, password);
        }
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }

    private ApplicationResponse handleLoginResult(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
            .orElse(null);
        if (user == null || !user.isPasswordValid(password)) {
            return new StaticFileResponse(HttpStatus.UNAUTHORIZED, "401");
        }
        logger.info(user.toString());
        Session session = buildSessionOfUser(user);
        JsonResponse response = new JsonResponse(HttpStatus.FOUND, "");
        response.addHeader(CommonHeaderKeys.SET_COOKIE.getKey(), "JSESSIONID=" + session.getId());
        response.addHeader(CommonHeaderKeys.LOCATION.getKey(), "/index.html");
        return response;
    }

    private Session buildSessionOfUser(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        sessionManager.add(session);
        return session;
    }

    public ApplicationResponse registerPage() {
        return new StaticFileResponse(HttpStatus.OK, "register");
    }

    public ApplicationResponse register(HttpRequest httpRequest) {
        String account = httpRequest.getBodyValueOf("account");
        String email = httpRequest.getBodyValueOf("email");
        String password = httpRequest.getBodyValueOf("password");
        User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
        Session session = buildSessionOfUser(newUser);
        JsonResponse response = new JsonResponse(HttpStatus.FOUND, "");
        response.addHeader(CommonHeaderKeys.SET_COOKIE.getKey(), "JSESSIONID=" + session.getId());
        response.addHeader(CommonHeaderKeys.LOCATION.getKey(), "/index.html");
        return response;
    }
}
