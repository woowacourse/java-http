package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.model.User;

public class LoginService {

    private final InMemoryUserRepository inMemoryUserRepository;

    public LoginService(InMemoryUserRepository inMemoryUserRepository) {
        this.inMemoryUserRepository = inMemoryUserRepository;
    }

    public User login(String account, String password) {
        final User foundUser = inMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnAuthorizedException("존재하지 않는 account 입니다."));
        foundUser.validatePassword(password);
        return foundUser;
    }
}
