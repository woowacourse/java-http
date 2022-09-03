package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService = new UserService();

    public void login(HttpRequest request) {
        Parameters parameters = request.getParameters();
        Optional<User> account = userService.findUser(parameters);
        if (account.isEmpty()) {
            return;
        }
        log.info(account.get().toString());
    }
}
