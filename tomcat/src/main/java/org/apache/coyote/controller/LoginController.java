package org.apache.coyote.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.exception.UserNotFoundException;
import org.apache.coyote.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected String doGet(final HttpRequest httpRequest) {
        return "/login.html";
    }

    @Override
    protected String doPost(final HttpRequest httpRequest) {
        Map<String, String> requestBody = httpRequest.getRequestBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
        if (user.checkPassword(password)) {
            log.info("user : {}", user);
        }

        return "redirect:/index.html";
    }

    @Override
    public boolean isRest() {
        return false;
    }
}
