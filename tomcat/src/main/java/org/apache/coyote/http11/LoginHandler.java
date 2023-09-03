package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final String QUERY_START_CHARACTER = "?";
    private static final int INVALID_INDEX = -1;

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    public LoginHandler() {
    }

    public boolean login(final String requestUrl) {
        final int index = requestUrl.indexOf("?");

        if (index == -1) {
            return false;
        }

        String queryString = requestUrl.substring(index + 1);
        final QueryParams queryParams = QueryParams.from(queryString);
        final String account = queryParams.getValueFromKey("account");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        final String userInformation = user.toString();
        log.info(userInformation);
        return user.checkPassword(queryParams.getValueFromKey("password"));
    }
}
