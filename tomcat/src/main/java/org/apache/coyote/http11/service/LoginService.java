package org.apache.coyote.http11.service;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public Optional<String> login(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && checkPassword(user.get(), password)) {
            return Optional.of(makeRandomUUID());
        }
        return Optional.empty();
    }

    private String makeRandomUUID() {
        return UUID.randomUUID().toString();
    }

    private boolean checkPassword(User user, String password) {
        boolean validLogin = user.checkPassword(password);
        if (validLogin) {
            log.info("{}", user);
        }
        return validLogin;
    }

    public String signUp(String account, String password, String email) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 유저입니다.");
        }
        InMemoryUserRepository.save(new User(account, password, email));
        return makeRandomUUID();
    }
}
