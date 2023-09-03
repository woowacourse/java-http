package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class UserService {
    public void login(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("일치하는 사용자가 없습니다.");
        }

        System.out.println("user: " + user);
    }
}
