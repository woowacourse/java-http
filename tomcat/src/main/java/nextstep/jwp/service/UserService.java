package nextstep.jwp.service;

import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.service.dto.LoginDto;
import nextstep.jwp.service.dto.SaveUserDto;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.support.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final InMemoryUserRepository userRepository;

    public UserService(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(LoginDto loginDto) {
        final var user = userRepository.findByAccount(loginDto.getAccount());
        if (user.isEmpty()) {
            throw new HttpException(HttpStatus.UNAUTHORIZED);
        }
        final var foundUser = user.get();
        if (!foundUser.checkPassword(loginDto.getPassword())) {
            throw new HttpException(HttpStatus.UNAUTHORIZED);
        }
        log.info("로그인 성공! - {}", user);
        return foundUser;
    }

    public void saveUser(SaveUserDto saveUserDto) {
        String account = saveUserDto.getAccount();
        final var user = userRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        final var savedUser = userRepository.save(new User(account, saveUserDto.getPassword(), saveUserDto.getEmail()));
        log.info("회원가입 성공! - {}", savedUser);
    }
}
