package nextstep.joanne.dashboard.service;

import nextstep.joanne.dashboard.db.InMemoryUserRepository;
import nextstep.joanne.dashboard.exception.LoginFailedException;
import nextstep.joanne.dashboard.exception.UserNotFoundException;
import nextstep.joanne.dashboard.model.User;

public class LoginService {
    public User login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
        if (user.invalidPassword(password)) {
            throw new LoginFailedException();
        }
        return user;
    }
}
