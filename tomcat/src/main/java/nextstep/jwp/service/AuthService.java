package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AlreadyRegisteredUserException;
import nextstep.jwp.exception.UnAuthorizationException;
import nextstep.jwp.exception.UnRegisteredUserException;
import nextstep.jwp.model.User;

public class AuthService {

    public User login(final String account, final String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UnRegisteredUserException::new);

        if (!user.checkPassword(password)) {
            throw new UnAuthorizationException();
        }

        return user;
    }

    public void register(final String account, final String email, final String password) {
        if(InMemoryUserRepository.isPresentAccount(account)){
            throw new AlreadyRegisteredUserException();
        }
        final User user = new User(account, email, password);
        InMemoryUserRepository.save(user);
    }
}
