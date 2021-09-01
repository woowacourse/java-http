package nextstep.jwp.application.controller;

import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.exception.UnauthorizedException;
import nextstep.jwp.application.model.User;
import nextstep.jwp.webserver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String body = readStaticFile("login.html");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", getContentType("html") + ";charset=utf-8");
        response.setHeaders(headers);
        response.setStatusCode(StatusCode._200_OK);
        response.setBody(body);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getBody("account");
        String password = request.getBody("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UnauthorizedException::new);

        if (!user.checkPassword(password)) {
            throw new UnauthorizedException();
        }
        log.info("user login success : user = " + user);

        response.setStatusCode(StatusCode._302_FOUND);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Location", "/index.html");
        response.setHeaders(headers);
    }
}
