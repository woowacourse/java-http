package org.apache.coyote.http11.handler;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequestStartLine;

public class LoginHandler extends Handler {

    public static LoginHandler LOGIN_HANDLER = new LoginHandler();
    
    LoginHandler() {
    }

    @Override
    public void handle(final HttpRequestStartLine requestStartLine) {
        final Map<String, String> queryParams = requestStartLine.getQueryParams();
        final Optional<User> user = InMemoryUserRepository.findByAccount(queryParams.get("account"));

        if (user.isPresent()) {
            final User loginUser = user.get();
            logLoginUser(queryParams, loginUser);
        }
    }

    private void logLoginUser(Map<String, String> queryParams, User loginUser) {
        if (loginUser.checkPassword(queryParams.get("password"))) {
            log.info("user : " + loginUser);
        }
    }
}
