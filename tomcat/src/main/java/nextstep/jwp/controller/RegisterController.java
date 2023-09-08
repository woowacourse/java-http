package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DuplicationMemberException;
import nextstep.jwp.model.User;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.ResponseEntity;
import org.apache.coyote.response.ResponseStatus;
import org.apache.exception.MethodMappingFailException;

import java.util.Optional;

public class RegisterController implements Controller {

    public static final String ACCOUNT_KEY = "account";
    public static final String PASSWORD_KEY = "password";
    public static final String EMAIL_KEY = "email";

    @Override
    public ResponseEntity handle(final Request request) {
        if (request.isPost()) {
            return join(request);
        }
        if (request.isGet()) {
            return joinPage(request);
        }
        throw new MethodMappingFailException();
    }

    private ResponseEntity join(final Request request) {
        final String account = request.getBodyValue(ACCOUNT_KEY);
        final String password = request.getBodyValue(PASSWORD_KEY);
        final String email = request.getBodyValue(EMAIL_KEY);

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new DuplicationMemberException();
        }
        final User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
        return ResponseEntity.fromViewPathWithRedirect(request.httpVersion(), request.getPath(), ResponseStatus.MOVED_TEMP, "/index.html");
    }

    private ResponseEntity joinPage(final Request request) {
        return ResponseEntity.fromViewPath(request.httpVersion(), request.getPath(), ResponseStatus.OK);
    }
}
