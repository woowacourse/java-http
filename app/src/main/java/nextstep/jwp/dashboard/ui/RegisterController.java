package nextstep.jwp.dashboard.ui;

import nextstep.jwp.dashboard.exception.DuplicateUserException;
import nextstep.jwp.web.controller.AbstractController;
import nextstep.jwp.dashboard.db.InMemoryUserRepository;
import nextstep.jwp.dashboard.domain.User;
import nextstep.jwp.web.controller.View;
import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.HttpResponse;
import nextstep.jwp.web.network.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    public RegisterController(String resource) {
        super(resource);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        log.info("GET /register");
        final View view = new View(getResource() + ".html");
        response.setStatus(HttpStatus.OK);
        response.setBody(view);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        final Map<String, String> payload = request.getBody();
        final User user = new User(
                payload.get("account"),
                payload.get("password"),
                payload.get("email")
        );
        if (InMemoryUserRepository.existsByAccount(user.getAccount())) {
            throw new DuplicateUserException(user.getAccount());
        }
        InMemoryUserRepository.save(user);
        log.info(String.format("New User Registered. user id : %d, account : %s", user.getId(), user.getAccount()));
        final View view = new View("/index");
        response.setStatus(HttpStatus.OK);
        response.setBody(view);
    }
}
