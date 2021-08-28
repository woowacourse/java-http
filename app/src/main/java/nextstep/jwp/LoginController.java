package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.network.HttpRequest;
import nextstep.jwp.network.HttpResponse;
import nextstep.jwp.network.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    public LoginController(String resource) {
        super(resource);
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        if (!httpRequest.toURI().hasQuery()) {
            return new HttpResponse(HttpStatus.OK, readFile(getResource()));
        } else {
            final Map<String, String> queryInfo = extractQuery(httpRequest.toURI().getQuery());
            final User user = InMemoryUserRepository.findByAccount(queryInfo.get("account"))
                    .orElseThrow(() -> new UserNotFoundException(queryInfo.get("account")));
            if (user.checkPassword(queryInfo.get("password"))) {
                log.info("Login successful!");
                return new HttpResponse(HttpStatus.FOUND, readFile(getResource()));
            } else {
                log.info("Login failed");
                return new HttpResponse(HttpStatus.UNAUTHORIZED, readFile(getResource()));
            }
        }
    }
}
