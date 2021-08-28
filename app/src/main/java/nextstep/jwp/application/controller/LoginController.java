package nextstep.jwp.application.controller;

import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.model.User;
import nextstep.jwp.webserver.Controller;
import nextstep.jwp.webserver.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public String handle(HttpRequest request) {
        String account = request.getQueryParam("account");
        String password = request.getQueryParam("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(RuntimeException::new);

        if (user.checkPassword(password)) {
            throw new RuntimeException();
        }

        log.info("user login success : user = " + user);

        return "Login 성공";
    }
}
