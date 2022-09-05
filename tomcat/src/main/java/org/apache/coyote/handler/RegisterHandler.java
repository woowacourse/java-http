package org.apache.coyote.handler;

import static org.apache.coyote.response.ContentType.HTML;
import static org.apache.coyote.response.StatusCode.FOUND;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.ExistUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.request.QueryParams;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler {

    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    private RegisterHandler() {
    }

    public static HttpResponse register(final String requestBody) {
        final QueryParams queryParams = QueryParams.from(requestBody);

        final String account = queryParams.getValueFromKey("account");
        final String password = queryParams.getValueFromKey("password");
        final String email = queryParams.getValueFromKey("email");

        checkAlreadyExistUser(account);

        final User user = new User(account, password, email);

        InMemoryUserRepository.save(user);

        final String userInformation = user.toString();
        log.info("회원가입 성공! : {}", userInformation);

        return HttpResponse.of(FOUND, HTML, "/index.html");
    }

    private static void checkAlreadyExistUser(String account) {
        final Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);
        if (foundUser.isPresent()) {
            throw new ExistUserException();
        }
    }
}
