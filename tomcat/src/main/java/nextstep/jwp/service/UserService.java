package nextstep.jwp.service;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import utils.ParseUtils;

public class UserService {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public Optional<User> login(final String body) {
        Map<String, String> parsedBody = ParseUtils.parse(body, "&", "=");
        Optional<User> user = InMemoryUserRepository.findByAccount(parsedBody.get(ACCOUNT));

        if (user.isEmpty()) {
            return user;
        }

        if (!user.get().checkPassword(parsedBody.get(PASSWORD))) {
            return Optional.empty();
        }

        return user;
    }

    public boolean register(final String body) {
        Map<String, String> parsedBody = ParseUtils.parse(body, "&", "=");
        String account = parsedBody.get("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return false;
        }
        User user = new User(account, parsedBody.get("password"), parsedBody.get("email"));
        InMemoryUserRepository.save(user);

        return true;
    }
}
