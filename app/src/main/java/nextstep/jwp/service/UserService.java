package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.service.exception.UserNotFoundException;
import nextstep.jwp.service.exception.UserPasswordInValidException;

public class UserService {

    public User login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);

        if (!user.checkPassword(password)) {
            throw new UserPasswordInValidException();
        }

        return user;
    }

    public void save(String account, String password, String email) {
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
