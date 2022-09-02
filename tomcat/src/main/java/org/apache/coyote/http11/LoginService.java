package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.util.Optional;
import java.util.UUID;

public class LoginService {

    private static final String SUCCESS_URL = "/index.html";
    private static final String FAIL_URL = "/401.html";

    public LoginResult signIn(String account, String password) {
        if (account == null || password == null) {
            return new LoginResult(FAIL_URL);
        }
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty()) {
            return new LoginResult(FAIL_URL);
        }
        User user = optionalUser.get();
        if (!user.checkPassword(password)) {
            return new LoginResult(FAIL_URL);
        }
        return saveSession(user);
    }

    private LoginResult saveSession(User user) {
        SessionManager sessionManager = new SessionManager();
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(session.getId(), user);
        sessionManager.add(session);
        return new LoginResult(SUCCESS_URL, session);
    }
}
