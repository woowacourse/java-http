package nextstep.jwp.model.web.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;

public class LoginService {

    public User login(String account, String password) {
        if (!isExistAccount(account)) {
            throw new RuntimeException("Account Not Exist!!");
        }
        User user = findByAccount(account);
        user.checkPassword(password);
        return user;
    }

    private boolean isExistAccount(String account) {
        return InMemoryUserRepository.isExistAccount(account);
    }
}
