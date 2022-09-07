package nextstep.jwp.presentation;

import java.util.NoSuchElementException;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.constant.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final LoginController instance = new LoginController();

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String ACCOUNT_PARAM = "account";
    private static final String PASSWORD_PARAM = "password";

    private static final String JSESSIONID = "JSESSIONID";

    private LoginController() {
    }

    public static LoginController getInstance() {
        return instance;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Session session = request.getSession(false);
        final User user = getUser(session);
        if (user != null) {
            redirectIndex(response);
        }

        final Controller staticResourceController = StaticResourceController.getInstance();
        staticResourceController.service(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        try {
            final String account = request.getBodyParam(ACCOUNT_PARAM);
            final String password = request.getBodyParam(PASSWORD_PARAM);
            Objects.requireNonNull(account, "null이면 안됨 ㅋ");
            Objects.requireNonNull(password, "null이면 안됨 ㅋ");

            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(NoSuchElementException::new);
            if (user.checkPassword(password)) {
                log.info(user.toString());

                final Session session = request.getSession(true);
                session.setAttribute("user", user);
                response.addSetCookie(JSESSIONID, session.getId());
                redirectIndex(response);
            } else {
                redirectNoAuth(response);
            }
        } catch (final RuntimeException e) {
            log.error(e.getMessage());
        }

        final Controller staticResourceController = StaticResourceController.getInstance();
        staticResourceController.service(request, response);
    }

    private User getUser(final Session session) {
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("user");
    }

    private static void redirectNoAuth(final HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setLocation("/401.html");
    }

    private static void redirectIndex(final HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setLocation("/index.html");
    }
}
