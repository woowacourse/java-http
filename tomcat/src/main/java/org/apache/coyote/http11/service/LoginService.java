package org.apache.coyote.http11.service;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.exception.MemberAlreadyExistsException;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private static void enrollSession(User user, String sessionId) {
        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        SessionManager.enroll(session);
    }

    public Optional<String> login(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && checkPassword(user.get(), password)) {
            String sessionId = makeRandomUUID();
            enrollSession(user.get(), sessionId);
            return Optional.of(sessionId);
        }
        return Optional.empty();
    }

    private String makeRandomUUID() {
        return UUID.randomUUID().toString();
    }

    private boolean checkPassword(User user, String password) {
        boolean validLogin = user.checkPassword(password);
        if (validLogin) {
            log.info("{}", user);
        }
        return validLogin;
    }

    public String signUp(String account, String password, String email) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new MemberAlreadyExistsException("이미 존재하는 유저입니다.");
        }
        User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
        String sessionId = makeRandomUUID();
        enrollSession(newUser, sessionId);
        return sessionId;
    }
}
