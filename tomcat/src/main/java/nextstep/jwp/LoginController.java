package nextstep.jwp;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    Logger log = LoggerFactory.getLogger(getClass());

    public void login(Map<String, String> params) {
        String account = params.get("account");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다 = " + account));

        log.info("user : {}", user);
    }
}
