package nextstep.jwp.dashboard.ui;

import nextstep.jwp.dashboard.exception.UserNotFoundException;
import nextstep.jwp.web.controller.AbstractController;
import nextstep.jwp.dashboard.db.InMemoryUserRepository;
import nextstep.jwp.dashboard.domain.User;
import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.HttpResponse;
import nextstep.jwp.web.network.response.HttpStatus;
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
        if (httpRequest.getURI().hasQuery()) {
            final Map<String, String> queryInfo = extractQuery(httpRequest.getURI().getQuery());
            final User user = InMemoryUserRepository.findByAccount(queryInfo.get("account"))
                    .orElseThrow(() -> new UserNotFoundException(queryInfo.get("account")));
            if (user.checkPassword(queryInfo.get("password"))) {
                log.info("Login successful!");
                return HttpResponse.ofByteArray(HttpStatus.FOUND, readIndex());
            } else {
                log.info("Login failed");
                return HttpResponse.ofByteArray(HttpStatus.UNAUTHORIZED, readHtmlFile("/401"));
            }
        } else {
            log.info("GET /login");
            return HttpResponse.ofByteArray(HttpStatus.OK, readHtmlFile(getResource()));
        }
    }
}
