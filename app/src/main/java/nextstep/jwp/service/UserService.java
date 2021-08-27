package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class UserService {

    public User login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(RuntimeException::new);

        if (!user.checkPassword(password)) {
            throw new RuntimeException();
        }

        return user;
    }

    public void save(String account, String password, String email) {
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
