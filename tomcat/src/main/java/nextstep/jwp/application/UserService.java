package nextstep.jwp.application;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class UserService {
    
    public boolean validateLogin(final String account, final String password) {
        return getUserByAccount(account).checkPassword(password);
    }

    public User getUserByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}
