package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User logIn(final Map<String, String> requestBody) {
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException(account));

        if (!user.checkPassword(password)) {
            throw new UnauthorizedException(account);
        }
        log.info("{}", user);

        return user;
    }

    public User register(final Map<String, String> requestBody) {
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");
        final String email = requestBody.get("email");

        InMemoryUserRepository.findByAccount(account)
                .ifPresent(ignored -> {
                    throw new UnauthorizedException(account);
                });

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return user;
    }

}
