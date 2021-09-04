package nextstep.jwp.dashboard.ui;

import nextstep.jwp.dashboard.db.InMemoryUserRepository;
import nextstep.jwp.dashboard.domain.User;
import nextstep.jwp.dashboard.exception.UserNotFoundException;
import nextstep.jwp.web.controller.AbstractController;
import nextstep.jwp.web.network.HttpSession;
import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    public LoginController(String resource) {
        super(resource);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        final HttpSession session = request.getSession();
        final User user = getUser(session);
        if (user == null) {
            log.info("GET /login");
            okWithResource(response);
        } else {
            log.info("GET /login, user {} already logged in. Redirecting to homepage.", user.getAccount());
            redirect(response, HOMEPAGE);
        }
    }

    private User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        try {
            final String account = request.getAttribute("account");
            final String password = request.getAttribute("password");
            final User user = findUser(account);

            if (isWrongPassword(user, password)) {
                log.info("Login failed");
                unauthorized(response);
                return;
            }

            log.info("Login successful! user account: {}", user.getAccount());
            final HttpSession session = addUserToSession(request, user);
            redirectWithSessionCookie(response, HOMEPAGE, session);
        } catch (UserNotFoundException e) {
            log.info(e.getMessage());
            unauthorized(response);
        }
    }

    private User findUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UserNotFoundException(account));
    }

    private boolean isWrongPassword(User user, String password) {
        return !user.checkPassword(password);
    }

    private HttpSession addUserToSession(HttpRequest request, User user) {
        final HttpSession session = request.getSession();
        session.setAttribute("user", user);
        log.info("saved user {} in session {}", user.getAccount(), session.getId());
        return session;
    }
}
