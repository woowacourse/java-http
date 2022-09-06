package org.apache.coyote.http11.handler;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;

public class LoginHandler extends Handler {

    public static LoginHandler LOGIN_HANDLER = new LoginHandler();

    LoginHandler() {
    }

    @Override
    public void handle(final HttpRequest request) {
        final HttpRequestBody requestBody = request.getBody();
        final Optional<User> user = InMemoryUserRepository.findByAccount(requestBody.getInfo("account"));

        if (user.isPresent()) {
            final User loginUser = user.get();
            logLoginUser(requestBody, loginUser);
        }
    }

    private void logLoginUser(final HttpRequestBody requestBody, User loginUser) {
        if (loginUser.checkPassword(requestBody.getInfo("password"))) {
            log.info("user : " + loginUser);
        }
    }
}
