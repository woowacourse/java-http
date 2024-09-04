package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.common.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public void handle(Request request) {
        if (request.getParameters().containsKey("account") && request.getParameters().containsKey("password")) {
            login(request);
        }
    }

    private void login(Request request) {
        String account = request.getParameters().get("account");
        String password = request.getParameters().get("password");
        User findUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new IllegalArgumentException("로그인 실패"));
        log.info("user: {}", findUser);
    }
}
