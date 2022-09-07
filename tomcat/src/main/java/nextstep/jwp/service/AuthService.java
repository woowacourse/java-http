package nextstep.jwp.service;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.dto.LoginRequest;
import nextstep.jwp.dto.UserDto;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.exception.WrongPasswordException;
import nextstep.jwp.model.User;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {

    private static final AuthService INSTANCE = new AuthService();
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private AuthService() {
    }

    public static AuthService getInstance() {
        return INSTANCE;
    }

    public String register(final UserDto request) {
        final User user = request.toUser();
        InMemoryUserRepository.save(user);

        final Session session = Session.generate();
        session.setAttribute("user", user);
        SessionManager.add(session);

        return session.getId();
    }

    public boolean alreadyLogin(final String sessionId) {
        final Session session = SessionManager.findSession(sessionId);
        if (session == null) {
            return false;
        }

        final User user = (User) session.getAttribute("user");
        if (user == null) {
            return false;
        }
        return InMemoryUserRepository.findByAccount(user.getAccount())
                .isPresent();
    }

    public String login(final LoginRequest request) {
        final String account = request.getAccount();
        final String password = request.getPassword();
        final Optional<User> possibleUser = InMemoryUserRepository.findByAccount(account);

        if (possibleUser.isEmpty()) {
            log.info("존재하지 않는 유저 [요청 account: {}]", account);
            throw new UserNotFoundException();
        }

        final User user = possibleUser.get();
        if (!user.checkPassword(password)) {
            log.info("비밀번호 불일치 [요청 account: {}]", account);
            throw new WrongPasswordException();
        }

        final Session session = Session.generate();
        session.setAttribute("user", user);
        SessionManager.add(session);

        log.info("로그인 성공 [요청 account: {}]", account);
        return session.getId();
    }
}
