package nextstep.jwp.presentation;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.http.common.HttpCookie;
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
    private static final String PREFIX = "static";

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
                File file = new File(Thread.currentThread().getContextClassLoader().getResource(PREFIX + "/index.html").getFile());
                httpResponse.addResponseBody(file);
                return;
            }
        }
        File file = new File(Thread.currentThread().getContextClassLoader().getResource(PREFIX + "/login.html").getFile());
        httpResponse.addResponseBody(file);
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
            addCookie(httpResponse, user);
            File file = new File(Thread.currentThread().getContextClassLoader().getResource(PREFIX + "/index.html").getFile());
            httpResponse.addResponseBody(file);
        } catch (NotFoundUserException e) {
            httpResponse.addHttpStatus(HttpStatus.UNAUTHORIZED);
            File file = new File(Thread.currentThread().getContextClassLoader().getResource(PREFIX + "/401.html").getFile());
            httpResponse.addResponseBody(file);
        }
    }

    private void validatePassword(final User user, final String password) {
        if (!user.checkPassword(password)) {
            throw new NotFoundUserException();
        }
    }

    // Cookie (JSESSIONID, UUID) -> Session(JSESSIONID, USER) -> SessionMap(UUID, USER)
    private void addCookie(final HttpResponse httpResponse, final User user) {
        HttpCookie httpCookie = SessionManager.createCookie();
        Session session = new Session(httpCookie.getKey());
        session.addAttribute("user", user);
        SessionManager.addSession(httpCookie.getValue(), session);
        httpResponse.addCookie(httpCookie);
    }
}
