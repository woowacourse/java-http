package org.apache.coyote.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterHandler {

    public boolean register(String account, String password, String email) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return false;
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return true;
    }
}
