package nextstep.jwp.service;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public String logIn(final Map<String, String> requestBody) {
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException(account));

        if (!user.checkPassword(password)) {
            throw new UnauthorizedException(account);
        }
        log.info("{}", user);

        return setSession(user);
    }

    private String setSession(final User user) {
        final String id = UUID.randomUUID().toString();
        final Session session = new Session(id);
        session.setAttribute("user", user);
        SessionManager.add(session);
        return id;
    }

    public String register(final Map<String, String> requestBody) {
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");
        final String email = requestBody.get("email");

        InMemoryUserRepository.findByAccount(account)
                .ifPresent(ignored -> {
                    throw new UnauthorizedException(account);
                });

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return setSession(user);
    }

}
