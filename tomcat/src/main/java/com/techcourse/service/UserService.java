package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.general.CommonHeaderKeys;
import org.apache.coyote.http11.general.Cookies;
import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.handler.applicationRequest.ApplicationRequest;
import org.apache.coyote.http11.handler.applicationResponse.ApplicationResponse;
import org.apache.coyote.http11.handler.applicationResponse.JsonResponse;
import org.apache.coyote.http11.handler.applicationResponse.StaticFileResponse;
import org.apache.coyote.http11.general.CookieParser;
import org.apache.coyote.http11.httpResponse.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final SessionManager sessionManager;

    public UserService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public ApplicationResponse loginPage(ApplicationRequest applicationRequest) {
        String sessionId = findSessionId(applicationRequest);
        if (sessionId == null || !isValidSession(sessionId)) {
            HttpHeaders headers = HttpHeaders.empty();
            headers.add(CommonHeaderKeys.LOCATION.getKey(), "/login.html");
            return new JsonResponse(HttpStatus.FOUND, headers, "");
        }
        HttpHeaders headers = HttpHeaders.empty();
        headers.add(CommonHeaderKeys.LOCATION.getKey(), "/index.html");
        return new JsonResponse(HttpStatus.FOUND, headers, "");
    }

    private String findSessionId(ApplicationRequest applicationRequest) {
        Cookies cookies = CookieParser.parseFromHeaders(applicationRequest.getHeaders());
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

    public ApplicationResponse login(ApplicationRequest applicationRequest) {
        String account = applicationRequest.getBodyValueOf("account");
        String password = applicationRequest.getBodyValueOf("password");
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
        HttpHeaders headers = HttpHeaders.empty();
        headers.add(CommonHeaderKeys.SET_COOKIE.getKey(), "JSESSIONID=" + session.getId());
        headers.add(CommonHeaderKeys.LOCATION.getKey(), "/index.html");
        return new JsonResponse(HttpStatus.FOUND, headers, "");
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

    public ApplicationResponse register(ApplicationRequest applicationRequest) {
        String account = applicationRequest.getBodyValueOf("account");
        String email = applicationRequest.getBodyValueOf("email");
        String password = applicationRequest.getBodyValueOf("password");
        User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
        Session session = buildSessionOfUser(newUser);
        HttpHeaders headers = HttpHeaders.empty();
        headers.add(CommonHeaderKeys.SET_COOKIE.getKey(), "JSESSIONID=" + session.getId());
        headers.add(CommonHeaderKeys.LOCATION.getKey(), "/index.html");
        return new JsonResponse(HttpStatus.FOUND, headers, "");
    }
}
