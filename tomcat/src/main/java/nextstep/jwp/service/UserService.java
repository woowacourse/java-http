package nextstep.jwp.service;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class UserService {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String REGEX_1 = "&";
    private static final String REGEX_2 = "=";

    public Optional<User> login(final Map<String, String> body) {
        Optional<User> user = InMemoryUserRepository.findByAccount(body.get(ACCOUNT));

        if (user.isEmpty()) {
            return user;
        }

        if (!user.get().checkPassword(body.get(PASSWORD))) {
            return Optional.empty();
        }

        return user;
    }

    public boolean register(final Map<String, String> body) {
        String account = body.get(ACCOUNT);
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return false;
        }
        User user = new User(account, body.get(PASSWORD), body.get(EMAIL));
        InMemoryUserRepository.save(user);

        return true;
    }
}
