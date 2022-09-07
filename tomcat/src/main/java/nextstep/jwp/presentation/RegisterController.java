package nextstep.jwp.presentation;

import static nextstep.jwp.presentation.StaticResource.INDEX_PAGE;
import static org.apache.catalina.Session.JSESSIONID;

import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.constant.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final RegisterController INSTANCE = new RegisterController();

    private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

    private static final String ACCOUNT_PARAM = "account";
    private static final String EMAIL_PARAM = "email";
    private static final String PASSWORD_PARAM = "password";

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setBody(StaticResource.ofRequest(request));
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        try {
            final User user = register(request);

            logSuccessMessage(user);
            setLoginSession(request, response, user);
            redirectIndex(response);
        } catch (final RuntimeException runtimeException) {
            LOG.error(runtimeException.getMessage());
        }

        response.setBody(StaticResource.ofRequest(request));
    }

    private User register(final HttpRequest request) {
        final String account = request.getBodyParam(ACCOUNT_PARAM);
        final String email = request.getBodyParam(EMAIL_PARAM);
        final String password = request.getBodyParam(PASSWORD_PARAM);

        checkNull(account, email, password);
        checkAccountDuplication(account);

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return user;
    }

    private void checkNull(final String account, final String email, final String password) {
        Objects.requireNonNull(account, "account는 null이면 안됩니다.");
        Objects.requireNonNull(email, "email은 null이면 안됩니다.");
        Objects.requireNonNull(password, "password는 null이면 안됩니다.");
    }

    private void checkAccountDuplication(final String account) {
        final Optional<User> savedAccount = InMemoryUserRepository.findByAccount(account);
        if (savedAccount.isPresent()) {
            throw new IllegalStateException("중복된 아이디입니다.");
        }
    }

    private void logSuccessMessage(final User user) {
        final String msg = user.toString();
        LOG.info(msg);
    }

    private void setLoginSession(final HttpRequest request, final HttpResponse response, final User user) {
        final Session session = request.getSession(true);
        session.setAttribute("user", user);
        response.addSetCookie(JSESSIONID, session.getId());
    }

    private void redirectIndex(final HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(INDEX_PAGE);
    }

}
