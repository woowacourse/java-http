package nextstep.jwp.service;

import nextstep.jwp.controller.dto.request.LoginRequest;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.model.User;

public class LoginService {

    public void login(LoginRequest loginRequest) {
        final User foundUser = InMemoryUserRepository.findByAccount(loginRequest.getAccount())
                .orElseThrow(() -> new UnAuthorizedException("존재하지 않는 account 입니다."));
        foundUser.validatePassword(loginRequest.getPassword());
    }
}
