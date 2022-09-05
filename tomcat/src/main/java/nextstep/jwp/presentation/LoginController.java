package nextstep.jwp.presentation;

import java.io.File;
import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.model.User;
import nextstep.jwp.http.response.HttpResponse;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String PREFIX = "static";

    public static LoginController instance = new LoginController();

    private LoginController() {
    }

    public static LoginController getInstance() {
        return instance;
    }

    @Override
    void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        File file = new File(Thread.currentThread().getContextClassLoader().getResource(PREFIX + "/login.html").getFile());
        httpResponse.addResponseBody(file);
    }

    @Override
    void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        String account = httpRequest.getQueryParameterValue("account");
        String password = httpRequest.getQueryParameterValue("password");
        try {
            User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NotFoundUserException::new);
            validatePassword(user, password);
            log.info("user : {}", user);
            File file = new File(Thread.currentThread().getContextClassLoader().getResource(PREFIX + "/login.html").getFile());
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
}
