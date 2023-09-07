package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.Session;

public class UserService {

    private UserService() {
    }

    public static boolean login(final String account, final String password,
            final Session session) {
        if (isAlreadyLogin(session)) {
            return false;
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
    }

    private static boolean isAlreadyLogin(final Session session) {
        if (session == null) {
            return false;
        }
        return SessionManager.findSession(session.getId()).isPresent();
    }

    public static boolean register(final String account, final String password,
            final String email) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return false;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        return true;
    }
}
