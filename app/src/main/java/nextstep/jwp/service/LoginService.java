package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginException;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.User;

public class LoginService {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public void loginByGet(Request request) {
        try {
            Map<String, String> queries = request.queries();
            String account = queries.get(ACCOUNT);
            String password = queries.get(PASSWORD);
            User user = getUser(account);
            checkPassword(user, password);
        } catch (IndexOutOfBoundsException | NullPointerException exception) {
            throw new LoginException("잘못된 로그인입니다.");
        }
    }

    public void loginByPost(Request request) {
        try {
            Map<String, String> queries = request.getRequestBody().queries();
            String account = queries.get(ACCOUNT);
            String password = queries.get(PASSWORD);
            User user = getUser(account);
            checkPassword(user, password);
        } catch (IndexOutOfBoundsException | NullPointerException exception) {
            throw new LoginException("잘못된 로그인입니다.");
        }
    }

    private void checkPassword(User user, String password) {
        if (!user.checkPassword(password)) {
            throw new LoginException("잘못된 로그인입니다.");
        }
    }

    private User getUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new LoginException("잘못된 로그인입니다."));
    }
}
