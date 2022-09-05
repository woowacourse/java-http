package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class LoginService {

    private final SessionManager sessionManager;

    public LoginService() {
        this.sessionManager = new SessionManager();
    }

    public String login(final Map<String, String> parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");

        final var user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));

        if (!user.checkPassword(password)) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }
        System.out.println("로그인에 성공했습니다! user: " + user);

        final Session session = makeNewSession(user);
        return session.getId();
    }

    private Session makeNewSession(final User user) {
        final var session = new Session();
        session.setAttribute("user", user);
        sessionManager.add(session);
        return session;
    }

    public boolean isAlreadyLogin(final Session session) {
        return sessionManager.findSession(session.getId()) != null;
    }
}
