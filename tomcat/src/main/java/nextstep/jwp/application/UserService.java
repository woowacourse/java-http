package nextstep.jwp.application;

import nextstep.jwp.application.exception.AlreadyExistsAccountException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.exception.LoginFailureException;

public class UserService {

    public User login(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                                                .orElseThrow(LoginFailureException::new);

        if (!user.checkPassword(password)) {
            throw new LoginFailureException();
        }

        return user;
    }

    public void register(final String account, final String password, final String email) {
        if (InMemoryUserRepository.existsByAccount(account)) {
            throw new AlreadyExistsAccountException();
        }

        final User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
    }
}
