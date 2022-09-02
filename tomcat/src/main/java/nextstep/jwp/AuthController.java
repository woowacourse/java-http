package nextstep.jwp;

import org.apache.coyote.http11.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private AuthController() {
    }

    public static void login(HttpRequest request) {
        final String account = request.getQueryValue("account");
        final String password = request.getQueryValue("password");

        User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(() -> new IllegalArgumentException("Account Not Found"));

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("Password Not Matched");
        }
        log.info("user : {}", user);
    }
}
