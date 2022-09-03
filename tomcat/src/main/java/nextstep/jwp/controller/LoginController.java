package nextstep.jwp.controller;

import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
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
    void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.haveParam(ACCOUNT_PARAM) && request.haveParam(PASSWORD_PARAM)) {
            final String account = request.getParam(ACCOUNT_PARAM);
            final String password = request.getParam(PASSWORD_PARAM);
            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(NoSuchElementException::new);
            if (user.checkPassword(password)) {
                final String outputMessage = user.toString();
                log.info(outputMessage);
            }
        }

        final StaticResourceController staticResourceController = StaticResourceController.getInstance();
        staticResourceController.service(request, response);
    }
}
