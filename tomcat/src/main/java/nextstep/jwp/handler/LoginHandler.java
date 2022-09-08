package nextstep.jwp.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static boolean login(final Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();

        if (user.checkPassword(password)) {
            log.info("로그인 성공! user: {}", user);
            return true;
        }
        return false;
    }
}
