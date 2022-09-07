package nextstep.jwp.presentation;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DuplicateUserException;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    public static final String REGISTER_PATH = "/register";

    public static RegisterController instance = new RegisterController();

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        return instance;
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        httpResponse.sendRedirect(HttpStatus.OK, "/register.html");
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        String account = httpRequest.getRequestBody().getValue("account");
        String email = httpRequest.getRequestBody().getValue("email");
        String password = httpRequest.getRequestBody().getValue("password");
        try {
            validateDuplicatedUser(InMemoryUserRepository.existByAccount(account));
            InMemoryUserRepository.save(new User(account, password, email));
            httpResponse.sendRedirect(HttpStatus.FOUND, "/index.html");
        } catch (DuplicateUserException e) {
            httpResponse.sendRedirect(HttpStatus.FOUND, "/register.html");
        }
    }

    private void validateDuplicatedUser(final boolean exist) {
        if (exist) {
            throw new DuplicateUserException();
        }
    }
}
