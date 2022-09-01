package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.exception.DuplicationException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.model.User;

public class Service {

    public void login(final Map<String, String> parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");

        final var user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));

        if (!user.checkPassword(password)) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }
        System.out.println("로그인에 성공했습니다! user: " + user);
    }

    public void register(final Map<String, String> parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        final var email = parameters.get("email");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new DuplicationException("이미 존재하는 아이디입니다.");
        }

        InMemoryUserRepository.save(new User(account, password, email));
    }
}
