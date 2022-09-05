package org.apache.coyote.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected String doGet(final HttpRequest httpRequest) {
        return "register";
    }

    @Override
    protected String doPost(final HttpRequest httpRequest) {
        Map<String, String> requestBody = httpRequest.getRequestBody();
        String account = requestBody.get("account");
        String email = requestBody.get("email");
        String password = requestBody.get("password");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("user : {}", user);

        return "redirect:/index";
    }

    @Override
    public boolean isRest() {
        return false;
    }
}
