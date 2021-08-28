package nextstep.jwp.webserver.controller;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Optional;

import nextstep.jwp.framework.context.AbstractController;
import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.util.Resources;
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
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        final String response;
        if (httpRequest.getQueries().isEmpty()) {
            response = Resources.readString("/login.html");
            return HttpResponse.ok()
                               .body(response)
                               .contentLength(response.getBytes().length)
                               .build();
        }

        final String account = httpRequest.getValueFromQuery("account");
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        LOGGER.debug("user : {}", user);

        if (user.isEmpty()) {
            response = Resources.readString("/401.html");
            return HttpResponse.found("/401.html")
                               .body(response)
                               .contentLength(response.getBytes().length)
                               .build();
        }

        response = Resources.readString("/index.html");
        return HttpResponse.found("/index.html")
                           .body(response)
                           .contentLength(response.getBytes().length)
                           .build();
    }
}
