package nextstep.jwp.service;

import static org.apache.coyote.http11.authorization.SessionManager.SESSION_MANAGER;

import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.LoginRequest;
import nextstep.jwp.dto.RegisterRequest;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.authorization.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    public static final UserService USER_SERVICE = new UserService();
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private UserService() {
    }

    public String login(final LoginRequest request) {
        final User loginUser = InMemoryUserRepository.findByAccount(request.getAccount())
                .orElseThrow(() -> new IllegalArgumentException("없는 회원입니다."));

        if (!loginUser.checkPassword(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        final String jSessionId = String.valueOf(UUID.randomUUID());
        createSession(jSessionId, loginUser);
        log.info("user : " + loginUser);

        return jSessionId;
    }

    public String signUp(final RegisterRequest request) {
        final String account = request.getAccount();
        final String password = request.getPassword();
        final String email = request.getEmail();

        InMemoryUserRepository.save(new User(account, password, email));
        final User savedUser = InMemoryUserRepository.findByAccount(account).get();

        final String jSessionId = String.valueOf(UUID.randomUUID());
        createSession(jSessionId, savedUser);
        log.info("user : " + savedUser);
        
        return jSessionId;
    }

    private void createSession(final String jSessionId, final User user) {
        final Session session = new Session(jSessionId);
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);
    }
}
