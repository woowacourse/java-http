package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DuplicatedAccountException;
import nextstep.jwp.exception.InvalidEmailFormException;
import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.model.User;

public class UserService {

    public User login(final String account, final String password) {
        User user = findUser(account);
        validatePassword(password, user);
        return user;
    }

    private User findUser(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UnAuthorizedException::new);
    }

    private void validatePassword(final String password, final User user) {
        if (!user.checkPassword(password)) {
            throw new UnAuthorizedException();
        }
    }

    public void register(final String account, final String password, final String email) {
        validateRegister(account, email);
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private void validateRegister(final String account, final String email) {
        validateDuplicatedAccount(account);
        validateEmail(email);
    }

    private void validateDuplicatedAccount(final String account) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new DuplicatedAccountException(account);
        }
    }

    private void validateEmail(final String email) {
        if (!email.contains("@")) {
            throw new InvalidEmailFormException(email);
        }
    }
}
