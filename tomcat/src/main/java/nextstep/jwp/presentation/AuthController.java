package nextstep.jwp.presentation;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.UserRequest;

import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.QueryParam;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.exception.ElementNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController implements Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Override
    public ResponseEntity run(final HttpHeader httpHeader, final HttpBody httpBody) {
        String path = httpHeader.getStartLine().split(" ")[1];

        if (path.startsWith("/login")) {
            return login(path);
        }
        return register(httpHeader, httpBody);
    }

    private ResponseEntity login(final String path) {
        final QueryParam queryParam = new QueryParam(path);
        if (queryParam.matchParameters("account") && queryParam.matchParameters("password")) {

            UserRequest userRequest = new UserRequest(queryParam.getValue("account"),
                    queryParam.getValue("password"));

            final Optional<User> user = InMemoryUserRepository.findByAccount(userRequest.getAccount());
            if (user.isPresent()) {
                LOGGER.info(user.get().toString());
                return new ResponseEntity(StatusCode.MOVED_TEMPORARILY, "/index.html");
            }
            return new ResponseEntity(StatusCode.UNAUTHORIZED, "/401.html");
        }
        throw new ElementNotFoundException();
    }

    private ResponseEntity register(final HttpHeader httpHeader, HttpBody httpBody) {
        String path = httpHeader.getStartLine().split(" ")[1];
        String method = httpHeader.getStartLine().split(" ")[0];

        if (path.startsWith("/register") && method.equals("POST")) {
            final String account = httpBody.getValue("account");
            final String password = httpBody.getValue("password");
            final String email = httpBody.getValue("email");

            final User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            return new ResponseEntity(StatusCode.MOVED_TEMPORARILY, "/index.html");
        }

        return new ResponseEntity(StatusCode.OK, path);
    }
}
