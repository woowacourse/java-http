package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnAuthenticatedException;
import nextstep.jwp.model.User;

public class AuthService {

    public User login(String account, String password) {
        validateNonNull(account, password);
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UnAuthenticatedException::new);
        if (!user.checkPassword(password)) {
            throw new UnAuthenticatedException();
        }
        return user;
    }

    private void validateNonNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                throw new UnAuthenticatedException();
            }
        }
    }

    public void signUp(String account, String email, String password) {
        validateNonNull(account, email, password);
        InMemoryUserRepository.save(new User(account, email, password));
    }
}
