package org.apache.coyote.http11;

import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;

import java.util.Optional;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFilter {

    private static final Logger log = LoggerFactory.getLogger(LoginFilter.class);
    private static final String LOGIN = "login";
    private static final String QUERY_PARAM_PREFIX = "?";
    private static final int NEXT_INDEX = 1;
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    public static void doFilter(final String requestURI) {
        if (requestURI.contains(LOGIN)) {
            final var queryString = getQueryParams(requestURI);

            final var resolvedRequest = ArgumentResolver.resolve(queryString);

            final Optional<User> user = findByAccount(resolvedRequest.get(ACCOUNT_KEY));
            if (user.isPresent()) {
                if (user.get().checkPassword(resolvedRequest.get(PASSWORD_KEY))) {
                    log.info(user.get().toString());
                }
            }
        }
    }

    private static String getQueryParams(final String requestURI) {
        int index = requestURI.indexOf(QUERY_PARAM_PREFIX);
        return requestURI.substring(index + NEXT_INDEX);
    }
}
