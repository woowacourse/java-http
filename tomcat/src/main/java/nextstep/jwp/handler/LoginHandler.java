package nextstep.jwp.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static void handle(Map<String, String> requestParam) {
        User user = InMemoryUserRepository.findByAccount(requestParam.get("account"))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (user.checkPassword(requestParam.get("password"))) {
            log.info(user.toString());
        }
    }
}
