package nextstep.jwp.presentation;

import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.constant.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final RegisterController instance = new RegisterController();

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private RegisterController() {
    }

    public static RegisterController getInstance() {
        return instance;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Controller staticResourceController = StaticResourceController.getInstance();
        staticResourceController.service(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        try {
            final String account = request.getBodyParam("account");
            final String email = request.getBodyParam("email");
            final String password = request.getBodyParam("password");

            Objects.requireNonNull(account, "account 가 null 입니다.");
            Objects.requireNonNull(email, "email 이 null 입니다.");
            Objects.requireNonNull(password, "password 가 null 입니다.");

            final Optional<User> savedAccount = InMemoryUserRepository.findByAccount(account);
            if (savedAccount.isPresent()) {
                throw new IllegalStateException("중복된 아이디입니다.");
            }
            InMemoryUserRepository.save(new User(account, password, email));
            redirectIndex(response);
        } catch (final RuntimeException e) {
            log.error(e.getMessage());
            redirectRegister(response);
        }
    }

    private void redirectRegister(final HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setLocation("/register.html");
    }

    private static void redirectIndex(final HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setLocation("/index.html");
    }
}
