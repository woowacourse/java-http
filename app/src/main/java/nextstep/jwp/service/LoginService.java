package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;

public class LoginService {

    private final InMemoryUserRepository userRepository;

    public LoginService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void login(String account, String password) {
        User user = findByUserAccount(account);
        user.checkPassword(password);
    }

    private User findByUserAccount(String account) {
        return userRepository.findByAccount(account)
            .orElseThrow(UnauthorizedException::new);
    }
}
