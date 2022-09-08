package nextstep.jwp.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static boolean register(final Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");
        final String email = params.get("email");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return false;
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("회원가입 성공! user: {}", user);

        return true;
    }
}
