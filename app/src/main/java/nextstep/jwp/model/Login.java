package nextstep.jwp.model;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;

public class Login {

    private Map<String, String> query;

    public Login(Map<String, String> query) {
        this.query = query;
    }

    public boolean isSuccess() {
        String account = query.get("account");
        Optional<User> wrappedUser = InMemoryUserRepository.findByAccount(account);
        return wrappedUser.isPresent();
    }
}
