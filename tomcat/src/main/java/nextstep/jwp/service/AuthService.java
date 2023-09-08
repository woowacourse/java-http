package nextstep.jwp.service;

import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final SessionManager sessionManager;

    public AuthService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public String login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정이거나 비밀번호가 틀렸습니다."));
        if (!user.isSamePassword(password)) {
            throw new IllegalArgumentException("존재하지 않는 계정이거나 비밀번호가 틀렸습니다.");
        }

        Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);

        return session.getId();
    }

    public String register(String account, String password, String email) {
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);

        return session.getId();
    }
}
