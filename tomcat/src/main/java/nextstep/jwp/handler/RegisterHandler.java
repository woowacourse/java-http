package nextstep.jwp.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static boolean handle(Map<String, String> body) {
        String account = body.get("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return false;
        }
        User user = new User(account, body.get("password"), body.get("email"));
        InMemoryUserRepository.save(user);

        return true;
    }
}
