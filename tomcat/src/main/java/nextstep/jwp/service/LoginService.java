package nextstep.jwp.service;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public void register(String account, String password, String email) {
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("회원가입 성공, User = {}", user);
    }

    public boolean login(String account, String password) {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isEmpty()) {
            log.info("로그인 실패 = 존재하지 않는 사용자, account = {}", account);
            return false;
        }
        User user = optionalUser.get();
        if (!user.checkPassword(password)) {
            log.info("로그인 실패 = 잘못된 패스워드");
            return false;
        }
        return true;
    }

    public User findUserByAccount(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. account = " + account));
    }
}
