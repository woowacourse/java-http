package org.apache.coyote.handler;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.request.Request;

public class LoginController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String LOGIN_EXCEPTION_MESSAGE = "회원 정보를 올바르게 입력해주세요.";

    public boolean login(Request request) {
        try {
            Optional<User> user = InMemoryUserRepository.findByAccount(request.getQueryStringValue(ACCOUNT));
            if (user.isEmpty()) {
                return false;
            }
            String password = request.getQueryStringValue(PASSWORD);
            if (user.get().checkPassword(password)) {
                System.out.println(user);
                return true;
            }
            return false;
        } catch (IllegalArgumentException e) {
            throw new LoginException(LOGIN_EXCEPTION_MESSAGE);
        }
    }
}
