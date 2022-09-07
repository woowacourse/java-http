package org.apache.coyote.http11.http11handler.user;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.user.User;

public class UserService {

    public boolean addNewUser(String account, String email, String password) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return false;
        }
        InMemoryUserRepository.save(new User(account, password, email));
        return true;
    }
}
