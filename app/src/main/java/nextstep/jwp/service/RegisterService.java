package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.RegisterException;
import nextstep.jwp.model.domain.User;

public class RegisterService {

    private static final String ACCOUNT = "account";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    public void register(Map<String, String> queries) {
        try {
            String account = queries.get(ACCOUNT);
            String email = queries.get(EMAIL);
            String password = queries.get(PASSWORD);
            validateDuplicate(account, email);
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
        } catch (IndexOutOfBoundsException | NullPointerException exception) {
            throw new RegisterException();
        }
    }

    private void validateDuplicate(String account, String email) {
        if (InMemoryUserRepository.existsByAccount(account)
                || InMemoryUserRepository.existsByEmail(email)) {
            throw new RegisterException();
        }
    }
}
