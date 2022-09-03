package nextstep.jwp.service;

import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.servlet.response.HttpResponse.HttpResponseBuilder;
import org.apache.coyote.support.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final InMemoryUserRepository userRepository;

    public UserService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public HttpResponse findUser(String account, String password) {
        final var user = userRepository.findByAccount(account);
        if (user.isEmpty()) {
            throw new HttpException(HttpStatus.UNAUTHORIZED);
        }
        final var foundUser = user.get();
        if (!foundUser.checkPassword(password)) {
            throw new HttpException(HttpStatus.UNAUTHORIZED);
        }
        log.info("로그인 성공! - {}", user);
        return new HttpResponseBuilder(HttpStatus.FOUND)
                .setLocation("/index.html")
                .build();
    }

    public HttpResponse saveUser(String account, String password, String email) {
        final var user = userRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        if (account.isBlank() || password.isBlank() || email.isBlank()) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        final var savedUser = userRepository.save(new User(account, password, email));
        log.info("회원가입 성공! - {}", savedUser);
        return new HttpResponseBuilder(HttpStatus.FOUND)
                .setLocation("/index.html")
                .build();
    }
}
