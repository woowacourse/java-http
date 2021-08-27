package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        log.debug("HTTP GET Login Request: {}", request.getUri());
        response.forward("/login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        log.debug("HTTP POST Login Request: {}", request.getUri());

        String account = request.getParameter("account");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(request.getParameter("password"))) {
            log.debug("User Login Success! account: {}", account);
            response.redirect("/index.html");
            return;
        }
        log.debug("User Login Fail!");
        response.redirect("/401.html");
    }
}
