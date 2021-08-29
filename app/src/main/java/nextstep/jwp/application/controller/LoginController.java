package nextstep.jwp.application.controller;

import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.model.User;
import nextstep.jwp.webserver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getQueryParam("account");
        String password = request.getQueryParam("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(RuntimeException::new);

        if (!user.checkPassword(password)) {
            throw new RuntimeException();
        }
        log.info("user login success : user = " + user);

        response.setStatusCode(StatusCode._302_FOUND);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Location", "/index.html");
        response.setHeaders(headers);
    }
}
