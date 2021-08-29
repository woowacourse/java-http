package nextstep.jwp.controller;

import static nextstep.jwp.http.response.HttpStatus.FOUND;
import static nextstep.jwp.http.response.HttpStatus.OK;
import static nextstep.jwp.http.response.HttpStatus.UNAUTHORIZED;
import static nextstep.jwp.model.UserInfo.ACCOUNT;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected View doGet(HttpRequest request, HttpResponse response) {
        log.debug("Login - HTTP GET Request");

        response.setLine(OK);
        response.setContentType("text/html;charset=utf-8");

        return new View(request.getPath());
    }

    @Override
    protected View doPost(HttpRequest request, HttpResponse response) {
        log.debug("Login - HTTP POST Request");

        Map<String, String> query = request.getQuery();
        Optional<User> user = InMemoryUserRepository.findByAccount(query.get(ACCOUNT.getInfo()));

        if (user.isPresent()) {
            response.setLine(FOUND);
            response.setContentType("text/html;charset=utf-8");

            return new View("/index");
        }
        response.setLine(UNAUTHORIZED);
        response.setContentType("text/html;charset=utf-8");

        return new View("/401");
    }
}
