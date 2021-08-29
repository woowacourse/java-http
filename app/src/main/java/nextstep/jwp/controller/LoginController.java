package nextstep.jwp.controller;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;
import nextstep.jwp.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected View doGet(HttpRequest request, HttpResponse response) {
        log.debug("Login - HTTP GET Request");

        response.setLine(HttpStatus.OK);
        response.setContentType("text/html;charset=utf-8");

        return new View(request.getPath());
    }

    @Override
    protected View doPost(HttpRequest request, HttpResponse response) {
        log.debug("Login - HTTP POST Request");

        Map<String, String> query = request.getQuery();
        Optional<User> user = InMemoryUserRepository.findByAccount(query.get("account"));

        if (user.isPresent()) {
            response.setLine(HttpStatus.FOUND);
            response.setContentType("text/html;charset=utf-8");

            return new View("/index");
        }
        response.setLine(HttpStatus.UNAUTHORIZED);
        response.setContentType("text/html;charset=utf-8");

        return new View("/401");
    }
}
