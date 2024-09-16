package org.apache.catalina;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.Session;
import java.util.UUID;

public class AuthManager {

    public static final String AUTHENTICATION_COOKIE_NAME = "JSESSIONID";
    private static final String ACCOUNT_NAME = "account";
    private static final String PASSWORD_NAME = "password";
    private static final String EMAIL_NAME = "email";

    private AuthManager() {
    }

    public static Session register(HttpRequest httpRequest) {
        if (httpRequest.getRequestBody() == null) {
            throw new IllegalArgumentException("인증 정보가 존재하지 않습니다.");
        }

        String account = httpRequest.getRequestBodyValue(ACCOUNT_NAME);
        String password = httpRequest.getRequestBodyValue(PASSWORD_NAME);
        String email = httpRequest.getRequestBodyValue(EMAIL_NAME);

        InMemoryUserRepository.save(new User(account, password, email));
        return authenticate(httpRequest);
    }

    public static Session authenticate(HttpRequest httpRequest) {
        if (httpRequest.getRequestBody() == null) {
            throw new IllegalArgumentException("인증 정보가 존재하지 않습니다.");
        }

        if (!isUserExist(httpRequest)) {
            throw new IllegalArgumentException("회원가입된 유저가 아닙니다.");
        }

        String uuid = UUID.randomUUID().toString();
        Session session = new Session(uuid);
        session.setAttribute("user", session);
        SessionManager.add(session);

        return session;
    }

    private static boolean isUserExist(HttpRequest httpRequest) {
        String account = httpRequest.getRequestBodyValue(ACCOUNT_NAME);
        String password = httpRequest.getRequestBodyValue(PASSWORD_NAME);

        return InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }

    public static boolean isAuthenticated(HttpRequest httpRequest) {
        return httpRequest.getCookie(AUTHENTICATION_COOKIE_NAME) != null
                && SessionManager.findSession(httpRequest.getCookie(AUTHENTICATION_COOKIE_NAME)) != null;
    }

    public static Session getAuthenticatedSession(HttpRequest httpRequest) {
        if (!isAuthenticated(httpRequest)) {
            throw new IllegalArgumentException("인증되지 않은 유저입니다.");
        }

        return SessionManager.findSession(httpRequest.getCookie(AUTHENTICATION_COOKIE_NAME));
    }
}
