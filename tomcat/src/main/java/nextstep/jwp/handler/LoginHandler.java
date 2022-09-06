package nextstep.jwp.handler;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static boolean handle(Map<String, String> requestParam) {
        Optional<User> user = InMemoryUserRepository.findByAccount(requestParam.get("account"));

        if (user.isPresent() && user.get().checkPassword(requestParam.get("password"))) {
            log.info(user.toString());
            return true;
        }
        return false;
    }
}
