package nextstep.jwp.handler.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handler.dto.LoginRequest;
import nextstep.jwp.handler.exception.UnauthorizedException;
import nextstep.jwp.model.User;

public class LoginService {

    public void login(LoginRequest loginRequest) {
        User user = InMemoryUserRepository.findByAccount(loginRequest.getAccount())
                .orElseThrow(UnauthorizedException::new);

        if (!user.checkPassword(loginRequest.getPassword())) {
            throw new UnauthorizedException();
        }
    }
}
