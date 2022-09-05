package nextstep.jwp.presentation;

import java.io.File;
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
    private static final String PREFIX = "static";

    public static RegisterController instance = new RegisterController();

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        return instance;
    }

    @Override
    void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        File file = new File(Thread.currentThread().getContextClassLoader().getResource(PREFIX + "/register.html").getFile());
        httpResponse.addResponseBody(file);
    }

    @Override
    void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        String account = httpRequest.getRequestBody().getValue("account");
        String email = httpRequest.getRequestBody().getValue("email");
        String password = httpRequest.getRequestBody().getValue("password");
        try {
            validateDuplicatedUser(InMemoryUserRepository.existByAccount(account));
            InMemoryUserRepository.save(new User(account, password, email));
            File file = new File(Thread.currentThread().getContextClassLoader().getResource(PREFIX + "/index.html").getFile());
            httpResponse.addHttpStatus(HttpStatus.FOUND);
            httpResponse.addRedirect(file, "/index.html");
        } catch (DuplicateUserException e) {
            // Todo : 예외처리 고민해보기
            File file = new File(Thread.currentThread().getContextClassLoader().getResource(PREFIX + "/register.html").getFile());
            httpResponse.addHttpStatus(HttpStatus.FOUND);
            httpResponse.addRedirect(file, "/register.html");
        }
    }

    private void validateDuplicatedUser(final boolean exist) {
        if (exist) {
            throw new DuplicateUserException();
        }
    }
}
