package nextstep.jwp.service;

import nextstep.jwp.controller.request.LoginRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;

public class LoginService {

    private final InMemoryUserRepository userRepository;

    public LoginService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void login(LoginRequest loginRequest) {
        User user = findByUserAccount(loginRequest.getAccount());
        user.checkPassword(loginRequest.getPassword());
    }

    private User findByUserAccount(String account) {
        return userRepository.findByAccount(account)
            .orElseThrow(UnauthorizedException::new);
    }
}
