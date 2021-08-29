package nextstep.jwp.application.service;

import nextstep.jwp.application.db.UserRepository;
import nextstep.jwp.application.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public boolean loginUser(String account, String password) {
        final User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("없는 사용자 입니다."));
        log.info("user logged in! account = {}", user.getAccount());
        return user.checkPassword(password);
    }

    public void register(String account, String password, String email) {
        final Optional<User> user = userRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 계정입니다.");
        }
        log.info("new user joined! account = {}", account);
        userRepository.save(new User(account, password, email));
    }
}
