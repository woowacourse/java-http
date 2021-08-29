package nextstep.jwp.webserver.controller;

import java.util.EnumSet;
import java.util.Optional;

import nextstep.jwp.framework.context.AbstractController;
import nextstep.jwp.framework.http.*;
import nextstep.jwp.webserver.db.InMemoryUserRepository;
import nextstep.jwp.webserver.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPageController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginPageController.class);

    public LoginPageController() {
        super("/login", EnumSet.of(HttpMethod.GET));
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        if (httpRequest.getQueries().isEmpty()) {
            return new ResourceResponseTemplate().ok("/login.html");
        }

        final String account = httpRequest.getValueFromQuery("account");
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        LOGGER.debug("user : {}", user);

        if (user.isEmpty()) {
            return new ResourceResponseTemplate().unauthorized("/401.html");
        }

        return new StringResponseTemplate().found("/index.html");
    }
}
