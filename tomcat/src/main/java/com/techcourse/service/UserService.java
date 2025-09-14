package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import common.session.Session;
import common.session.SessionManager;
import java.util.NoSuchElementException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.NONE)
@Slf4j
public class UserService {

    private static final UserService INSTANCE = new UserService();

    public static UserService getInstance() {
        return INSTANCE;
    }

    public User authenticate(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NoSuchElementException("계정을 찾을 수 없습니다."));

        user.checkPassword(password);
        log.debug("사용자 인증 성공: {}", account);
        return user;
    }

    public User registerUser(final String account, final String password, final String email) {
        final User newUser = User.withoutId(account, password, email);
        final User savedUser = InMemoryUserRepository.save(newUser);
        log.debug("사용자 등록 성공: {}", savedUser);
        return savedUser;
    }

    public Session createSession(final User user) {
        final Session session = new Session();
        session.setAttribute(session.getId(), user);
        SessionManager.getInstance().add(session);
        log.debug("세션 생성: {} for user {}", session.getId(), user.getAccount());
        return session;
    }

    public boolean isValidSession(final Session session) {
        return SessionManager.getInstance().isValidSession(session.getId());
    }
}
