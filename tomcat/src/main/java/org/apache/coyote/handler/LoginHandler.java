package org.apache.coyote.handler;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginHandler {

    public boolean login(String account, String password) {

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return false;
        }
        System.out.println(user.get());
        return true;
    }
}
