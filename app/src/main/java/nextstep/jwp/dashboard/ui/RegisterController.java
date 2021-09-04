package nextstep.jwp.dashboard.ui;

import nextstep.jwp.dashboard.db.InMemoryUserRepository;
import nextstep.jwp.dashboard.domain.User;
import nextstep.jwp.dashboard.exception.DuplicateUserException;
import nextstep.jwp.web.controller.AbstractController;
import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    public RegisterController(String resource) {
        super(resource);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        log.info("GET /register");
        okWithResource(response);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        final User user = new User(
                request.getAttribute("account"),
                request.getAttribute("password"),
                request.getAttribute("email")
        );
        validateNew(user);
        InMemoryUserRepository.save(user);
        log.info("New User Registered. user id : {}, account : {}", user.getId(), user.getAccount());
        redirect(response, HOMEPAGE);
    }

    private void validateNew(User user) {
        if (exists(user)) {
            throw new DuplicateUserException(user.getAccount());
        }
    }

    private boolean exists(User user) {
        return InMemoryUserRepository.existsByAccount(user.getAccount());
    }
}
