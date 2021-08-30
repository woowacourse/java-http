package nextstep.jwp.controller;

import static nextstep.jwp.http.response.HttpStatus.CREATED;
import static nextstep.jwp.http.response.HttpStatus.OK;
import static nextstep.jwp.model.UserInfo.ACCOUNT;
import static nextstep.jwp.model.UserInfo.EMAIL;
import static nextstep.jwp.model.UserInfo.PASSWORD;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.controller.DuplicateRegisterException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    protected View doGet(HttpRequest request, HttpResponse response) {
        log.debug("Register - HTTP GET Request");

        response.forward(OK, request.getUri());

        return new View(request.getPath());
    }

    @Override
    protected View doPost(HttpRequest request, HttpResponse response) {
        log.debug("Register - HTTP POST Request");

        Map<String, String> query = request.getQuery();
        Optional<User> findUser = InMemoryUserRepository
            .findByAccount(query.get(ACCOUNT.getInfo()));

        if (findUser.isEmpty()) {
            User user = new User(
                query.get(ACCOUNT.getInfo()),
                query.get(PASSWORD.getInfo()),
                query.get(EMAIL.getInfo())
            );
            InMemoryUserRepository.save(user);

            response.forward(CREATED, request.getUri());
            return new View("/index");
        }
        throw new DuplicateRegisterException();
    }
}
