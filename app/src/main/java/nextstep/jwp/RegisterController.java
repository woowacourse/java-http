package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.network.HttpRequest;
import nextstep.jwp.network.HttpResponse;
import nextstep.jwp.network.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    public RegisterController(String resource) {
        super(resource);
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        return new HttpResponse(HttpStatus.OK, readFile(getResource()));
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest) {
        final Map<String, String> payload = httpRequest.getBody();
        final User user = new User(
                1L,
                payload.get("account"),
                payload.get("password"),
                payload.get("email")
        );
        InMemoryUserRepository.save(user);
        log.info(String.format("New User Registered. user id : %d, account : %s", user.getId(), user.getAccount()));
        return new HttpResponse(HttpStatus.OK, readIndex());
    }
}
