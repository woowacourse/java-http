package nextstep.jwp.controller;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handle.ViewResolver;
import nextstep.jwp.model.User;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.Session;
import org.apache.coyote.common.SessionManager;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String SLASH = File.separator;
    private static final String REGISTER_PAGE = "register.html";
    private static final String REGISTER_SUCCESS_PAGE = "index.html";
    private static final String REGISTER_FAIL_PAGE = "400.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String JSESSIONID = "JSESSIONID";
    private static final RegisterController registerController = new RegisterController();

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        return registerController;
    }

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        ViewResolver.renderPage(response, HttpStatus.OK, REGISTER_PAGE);
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Map<String, String> body = request.getBody(ContentType.APPLICATION_JSON);
        final String account = body.get(ACCOUNT);
        final String password = body.get(PASSWORD);
        final String email = body.get(EMAIL);
        if (account == null || password == null || email == null) {
            log.warn("Account Or Password Or Email Not Exist");
            response.setStatus(HttpStatus.FOUND);
            response.setLocation(SLASH + REGISTER_FAIL_PAGE);
            return;
        }

        final Optional<User> findUser = InMemoryUserRepository.findByAccount(body.get(ACCOUNT));
        if (findUser.isPresent()) {
            log.warn("Registered Account");
            response.setStatus(HttpStatus.FOUND);
            response.setLocation(SLASH + REGISTER_FAIL_PAGE);
            return;
        }
        final User user = new User(account, password, email);
        registerSuccess(response, user);
    }

    private void registerSuccess(final HttpResponse response, final User user) {
        InMemoryUserRepository.save(user);
        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        SessionManager.add(session);
        response.addCookie(JSESSIONID, session.getId());
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(SLASH + REGISTER_SUCCESS_PAGE);
    }
}
