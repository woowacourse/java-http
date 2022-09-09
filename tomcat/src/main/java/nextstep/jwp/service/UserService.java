package nextstep.jwp.service;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import utils.ParseUtils;

public class UserService {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String REGEX_1 = "&";
    private static final String REGEX_2 = "=";

    public Optional<User> login(final String body) {
        Map<String, String> parsedBody = ParseUtils.parse(body, REGEX_1, REGEX_2);
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
        Map<String, String> parsedBody = ParseUtils.parse(body, REGEX_1, REGEX_2);
        String account = parsedBody.get(ACCOUNT);
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return false;
        }
        User user = new User(account, parsedBody.get(PASSWORD), parsedBody.get(EMAIL));
        InMemoryUserRepository.save(user);

        return true;
    }
}
