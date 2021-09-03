package nextstep.jwp.application.controller;

import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.exception.UnauthorizedException;
import nextstep.jwp.application.model.User;
import nextstep.jwp.webserver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class LoginController extends AbstractController {

    private static final String MAPPING_URL = "/login";
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public String mappingUri() {
        return MAPPING_URL;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (Objects.nonNull(request.getSession().getUser())) {
            response.addHeaders("Location", "/index.html");
            response.setStatusCode(StatusCode._302_FOUND);
            return;
        }

        String body = readStaticFile("login.html");

        response.addHeaders("Content-Type", getContentType("html") + ";charset=utf-8");
        response.setStatusCode(StatusCode._200_OK);
        response.setBody(body);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        User user = findUserFromRepository(request);
        request.getSession().setUser(user);

        log.info("user login success : user = " + user);

        response.addHeaders("Location", "/index.html");
        response.setStatusCode(StatusCode._302_FOUND);
    }

    private User findUserFromRepository(HttpRequest request) {
        String account = request.getBody("account");
        String password = request.getBody("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UnauthorizedException::new);

        if (!user.checkPassword(password)) {
            throw new UnauthorizedException();
        }
        return user;
    }
}
