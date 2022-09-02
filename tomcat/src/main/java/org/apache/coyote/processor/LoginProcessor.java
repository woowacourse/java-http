package org.apache.coyote.processor;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginProcessor {

    private static final String QUERY_START_CHARACTER = "?";
    private static final int INVALID_INDEX = -1;

    private static final Logger log = LoggerFactory.getLogger(LoginProcessor.class);

    private LoginProcessor() {
    }

    public static void login(final String requestUrl) {
        final int index = requestUrl.indexOf(QUERY_START_CHARACTER);

        if (index == INVALID_INDEX) {
            return;
        }

        String queryString = requestUrl.substring(index + 1);
        final QueryProcessor queryProcessor = QueryProcessor.from(queryString);
        final String account = queryProcessor.getParameter("account");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchUserException::new);
        final String userInformation = user.toString();
        log.info(userInformation);
    }
}
