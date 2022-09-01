package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.NotFoundException;

public class Service {

    public void login(final Map<String, String> parameters) {
        final var account = parameters.get("account");
        final var user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));

        final var password = parameters.get("password");
        if (!user.checkPassword(password)) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }
        System.out.println("로그인에 성공했습니다! user: " + user);
    }
}
