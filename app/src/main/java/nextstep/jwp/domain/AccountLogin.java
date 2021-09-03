package nextstep.jwp.domain;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class AccountLogin {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    private Map<String, String> query;

    public AccountLogin(Map<String, String> query) {
        this.query = query;
    }

    public boolean isSuccess() {
        final String account = query.get(ACCOUNT);
        final String password = query.get(PASSWORD);
        final Optional<User> wrappedUser = InMemoryUserRepository.findByAccount(account);
        return wrappedUser.isPresent() && wrappedUser.get().checkPassword(password);
    }
}
