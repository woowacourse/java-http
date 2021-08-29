package nextstep.jwp.handler.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handler.dto.LoginRequest;
import nextstep.jwp.model.User;

public class LoginService {

    public void login(LoginRequest loginRequest) {
        String account = loginRequest.getAccount();
        String password = loginRequest.getPassword();

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 맞지 않습니다.");
        }
    }
}
