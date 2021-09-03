package nextstep.jwp.domain;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class SignUp {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    private Map<String, String> bodyQuery;

    public SignUp(Map<String, String> bodyQuery) {
        this.bodyQuery = bodyQuery;
    }

    public boolean isAble() {
        final String account = bodyQuery.get(ACCOUNT);
        final Optional<User> wrappedUser = InMemoryUserRepository.findByAccount(account);
        return wrappedUser.isEmpty();
    }

    public void process() {
        final long id = InMemoryUserRepository.nextId();
        final String account = bodyQuery.get(ACCOUNT);
        final String password = bodyQuery.get(PASSWORD);
        final String email = bodyQuery.get(EMAIL);
        final User signUpUser = new User(id, account, password, email);
        InMemoryUserRepository.save(signUpUser);
    }
}
