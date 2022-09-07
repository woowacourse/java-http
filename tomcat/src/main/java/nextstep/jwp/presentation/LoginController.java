package nextstep.jwp.presentation;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.common.Session;
import nextstep.jwp.http.common.SessionManager;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.model.User;
import nextstep.jwp.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    public static LoginController instance = new LoginController();

    private LoginController() {
    }

    public static LoginController getInstance() {
        return instance;
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        Optional<String> sessionValue = httpRequest.getJSessionValue();
        if (sessionValue.isPresent()) {
            Optional<Session> session = SessionManager.findSession(sessionValue.get());
            if (session.isPresent()) {
                httpResponse.sendRedirect(HttpStatus.OK, "/index.html");
                return;
            }
        }
        httpResponse.sendRedirect(HttpStatus.OK, "/login.html");
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        String account = httpRequest.getRequestBody().getValue("account");
        String password = httpRequest.getRequestBody().getValue("password");
        try {
            User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NotFoundUserException::new);
            validatePassword(user, password);
            log.info("user : {}", user);
            httpResponse.addCookie(user);
            httpResponse.sendRedirect(HttpStatus.FOUND, "/index.html");
        } catch (NotFoundUserException e) {
            httpResponse.sendError(HttpStatus.UNAUTHORIZED, "/index.html");
        }
    }

    private void validatePassword(final User user, final String password) {
        if (!user.checkPassword(password)) {
            throw new NotFoundUserException();
        }
    }
}
