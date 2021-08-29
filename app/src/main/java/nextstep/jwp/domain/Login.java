package nextstep.jwp.domain;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class Login {

    private Map<String, String> query;

    public Login(Map<String, String> query) {
        this.query = query;
    }

    public boolean isSuccess() {
        final String account = query.get("account");
        final Optional<User> wrappedUser = InMemoryUserRepository.findByAccount(account);
        return wrappedUser.isPresent();
    }
}
