package org.apache.coyote.handler;

import static org.apache.coyote.response.ContentType.HTML;
import static org.apache.coyote.response.StatusCode.FOUND;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.request.QueryParams;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private LoginHandler() {
    }

    public static HttpResponse login(final String requestBody) {
        final QueryParams queryParams = QueryParams.from(requestBody);

        final String account = queryParams.getValueFromKey("account");
        final String password = queryParams.getValueFromKey("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchUserException::new);

        if (!user.checkPassword(password)) {
            return HttpResponse.of(FOUND, HTML, "/401.html");
        }

        final String userInformation = user.toString();
        log.info(userInformation);

        return HttpResponse.of(FOUND, HTML, "/index.html");
    }
}
