package org.apache.coyote.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.exception.UserNotFoundException;
import org.apache.coyote.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public String service(final HttpRequest httpRequest) {
        Map<String, String> queryParams = httpRequest.getQueryParams();
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
        if (user.checkPassword(password)) {
            log.info("user : {}", user);
        }

        return "/login.html";
    }

    @Override
    public boolean isRest() {
        return false;
    }
}
