package nextstep.jwp.Handler;

import static nextstep.jwp.Handler.StaticHandler.handleStatic;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static String handle(HttpRequest request) throws IOException {
        QueryString queryString = request.queryString();
        if (queryString.isEmpty()) {
            return handleStatic("/login.html");
        }
        String account = queryString.get("account");
        String password = queryString.get("password");

        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isEmpty() || !userOptional.get().checkPassword(password)) {
            return handleStatic("/401.html");
        }
        User user = userOptional.get();
        log.info(user.toString());
        return handleStatic("/login.html");
    }
}
