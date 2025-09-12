package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.exception.Http4xxException;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);


    public User findUser(final String account, final String password, final Http11Response response) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            return user.get();
        }
        throw new UnauthorizedException(response);
    }

    public void saveUser(Http11Response response, String account, String password, String email) {
        checkDuplication(response, account);
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("User created: {}", user);
    }

    private void checkDuplication(Http11Response response, String account) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new Http4xxException("이미 가입한 사용자입니다.", response, HttpStatus.BAD_REQUEST);
        }
    }
}
