package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);
    private static final String LOGIN_ERROR_MESSAGE = "비밀번호가 맞지 않습니다.";

    public boolean login(Map<String, String> queryParams) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();

        if (checkValidPassword(user, password)) {
            log.info(user.toString());
            return true;
        }
        return false;
    }

    private boolean checkValidPassword(User user, String password) {
        if (!user.checkPassword(password)) {
            log.warn(LOGIN_ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
