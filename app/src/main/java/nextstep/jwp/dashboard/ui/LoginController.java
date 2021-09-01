package nextstep.jwp.dashboard.ui;

import nextstep.jwp.dashboard.exception.UserNotFoundException;
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
import java.util.UUID;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    public LoginController(String resource) {
        super(resource);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        log.info("GET /login");
        final View view = new View(getResource() + ".html");
        response.setStatus(HttpStatus.OK);
        response.setBody(view);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            final Map<String, String> queryInfo = request.bodyAsMap();
            final User user = InMemoryUserRepository.findByAccount(queryInfo.get("account"))
                    .orElseThrow(() -> new UserNotFoundException(queryInfo.get("account")));
            if (user.checkPassword(queryInfo.get("password"))) {
                log.info("Login successful! user account: {}", user.getAccount());

                response.setStatus(HttpStatus.FOUND);

                final UUID jsessionid = UUID.randomUUID();
                response.setHeader("Set-Cookie", "JSESSIONID=" + jsessionid);
                response.setHeader("Location", "/index.html");
                log.info("Sent jsessionid to user {}. jsessionid: {}", user.getAccount(), jsessionid);
            } else {
                log.info("Login failed");
                final View view = new View("/401");
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setBody(view);
            }
        } catch (UserNotFoundException e) {
            log.info(e.getMessage());
            final View view = new View("/401");
            response.setStatus(HttpStatus.UNAUTHORIZED);
            response.setBody(view);
        }
    }
}
