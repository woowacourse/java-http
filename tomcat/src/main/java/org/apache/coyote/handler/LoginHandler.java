package org.apache.coyote.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.request.QueryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final String QUERY_START_CHARACTER = "?";
    private static final int INVALID_INDEX = -1;

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private LoginHandler() {
    }

    public static String login(final String requestUrl) {
        final int index = requestUrl.indexOf(QUERY_START_CHARACTER);

        if (index == INVALID_INDEX) {
            return "/login.html";
        }

        String queryString = requestUrl.substring(index + 1);
        final QueryParams queryParams = QueryParams.from(queryString);
        final String account = queryParams.getValueFromKey("account");
        final String password = queryParams.getValueFromKey("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchUserException::new);

        if (!user.checkPassword(password)) {
            return "/401.html";
        }

        final String userInformation = user.toString();
        log.info(userInformation);

        return "/index.html";
    }
}
