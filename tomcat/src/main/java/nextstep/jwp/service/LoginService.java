package nextstep.jwp.service;

import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InvalidLoginInfoException;
import nextstep.jwp.model.User;
import nextstep.org.apache.coyote.http11.Session;
import nextstep.org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private final Logger log = LoggerFactory.getLogger(LoginService.class);

    public Session login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        if (!user.checkPassword(password)) {
            throw new InvalidLoginInfoException();
        }
        log.info(user.toString());
        return createSession(user);
    }

    private Session createSession(User user) {
        SessionManager sessionManager = new SessionManager();
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(user.getAccount(), user);
        sessionManager.add(session);
        return session;
    }

    public void register(String account, String password, String email) {
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
