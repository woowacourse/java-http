package nextstep.jwp.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static boolean handle(Map<String, String> requestParam) {
        User user = new User(requestParam.get("account"), requestParam.get("password"), requestParam.get("email"));
        InMemoryUserRepository.save(user);

        return true;
    }
}
