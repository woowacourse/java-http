package nextstep.jwp.application.service;

import nextstep.jwp.application.db.UserRepository;
import nextstep.jwp.application.model.User;
import nextstep.jwp.framework.http.common.HttpSession;
import nextstep.jwp.framework.http.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public boolean loginUser(final String account, final String password, final HttpRequest httpRequest) {
        final Optional<User> user = userRepository.findByAccount(account);
        if (user.isEmpty()) {
            return false;
        }

        final User loginUser = user.get();
        final boolean isProperUser = loginUser.checkPassword(password);
        if (isProperUser) {
            log.info("########## 유저 로그인! 계정 = {} ##########", loginUser.getAccount());
            final HttpSession httpSession = httpRequest.generateSession();
            httpSession.setAttribute("user", loginUser);
            return true;
        }
        return false;
    }

    public void register(String account, String password, String email) {
        final Optional<User> user = userRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 계정입니다.");
        }

        log.info("########## 신규 회원가입! 계정 = {} ##########", account);
        userRepository.save(new User(account, password, email));
    }
}
