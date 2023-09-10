package nextstep.jwp.service;

import java.util.Objects;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final SessionManager sessionManager;

    public AuthService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public boolean isLoggedIn(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        return Objects.nonNull(sessionManager.findSession(sessionId));
    }

    public String login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정이거나 비밀번호가 틀렸습니다."));
        validatePassword(password, user);

        log.info("login:" + user);

        Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        return session.getId();
    }

    private void validatePassword(String password, User user) {
        if (!user.isSamePassword(password)) {
            throw new IllegalArgumentException("존재하지 않는 계정이거나 비밀번호가 틀렸습니다.");
        }
    }

    public String register(String account, String password, String email) {
        validateAccountDuplication(account);
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        log.info("register:" + user);

        Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        return session.getId();
    }

    private void validateAccountDuplication(String account) {
        if (InMemoryUserRepository.findByAccount(account)
                .isPresent()) {
            log.info("중복된 계정");
            throw new IllegalArgumentException("중복된 계정 입니다.");
        }
    }
}
