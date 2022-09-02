package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public void login(Map<String, String> queryParam) {
        if (queryParam.isEmpty()) {
            return;
        }

        String account = queryParam.get("account");
        String password = queryParam.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        if (user.checkPassword(password)) {
            log.info(user.toString());
        }
        if (!user.checkPassword(password)) {
            log.warn("비밀번호가 맞지 않습니다.");
        }
    }
}
