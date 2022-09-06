package nextstep.jwp.service;

import java.util.NoSuchElementException;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class Service {

    public void login(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchElementException::new);

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("로그인에 실패했습니다.");
        }
    }
}
