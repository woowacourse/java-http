package nextstep.jwp.dashboard.ui;

import nextstep.jwp.dashboard.db.InMemoryUserRepository;
import nextstep.jwp.dashboard.domain.User;
import nextstep.jwp.dashboard.exception.UserNotFoundException;
import nextstep.jwp.web.controller.AbstractController;
import nextstep.jwp.web.controller.View;
import nextstep.jwp.web.network.HttpSession;
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
    public void doGet(HttpRequest request, HttpResponse response) {
        final HttpSession session = request.getSession();
        final User user = (User) session.getAttribute("user");
        if (user == null) {
            log.info("GET /login");
            final View view = new View(getResource() + ".html");
            response.setStatus(HttpStatus.OK);
            response.setBody(view);
        }
    }

    private User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            final Map<String, String> queryInfo = request.getBodyAsMap();
            final User user = InMemoryUserRepository.findByAccount(queryInfo.get("account"))
                    .orElseThrow(() -> new UserNotFoundException(queryInfo.get("account")));
            if (user.checkPassword(queryInfo.get("password"))) {
                log.info("Login successful! user account: {}", user.getAccount());

                final HttpSession session = request.getSession();

                response.setStatus(HttpStatus.FOUND);
                response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId());
                response.setHeader("Location", "/index.html");
                log.info("Sent jsessionid to user {}. jsessionid: {}", user.getAccount(), session.getId());
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
