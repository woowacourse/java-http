package nextstep.jwp.presentation;

import java.util.NoSuchElementException;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
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

    private LoginController() {
    }

    public static LoginController getInstance() {
        return instance;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        try {
            final String account = request.getParam(ACCOUNT_PARAM);
            final String password = request.getParam(PASSWORD_PARAM);
            Objects.requireNonNull(account, "null이면 안됨 ㅋ");
            Objects.requireNonNull(password, "null이면 안됨 ㅋ");

            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(NoSuchElementException::new);
            if (user.checkPassword(password)) {
                final String successMessage = user.toString();
                log.info(successMessage);
                response.setStatus(HttpStatus.FOUND);
                response.setLocation("/index.html");
            } else {
                response.setStatus(HttpStatus.FOUND);
                response.setLocation("/401.html");
            }
        } catch (final RuntimeException e) {
            log.error(e.getMessage());
        }

        final Controller staticResourceController = StaticResourceController.getInstance();
        staticResourceController.service(request, response);
    }
}
