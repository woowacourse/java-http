package org.apache.coyote.http11;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class UserService {

    public void register(String account, String email, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 계정입니다.");
        }
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
