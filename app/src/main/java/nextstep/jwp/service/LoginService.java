package nextstep.jwp.service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginException;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.User;

public class LoginService {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    public void loginByGet(Request request) throws IOException {
        Map<String, String> queries = request.queries();
        String account = queries.get(ACCOUNT);
        String password = queries.get(PASSWORD);
        Optional<User> dbUser = InMemoryUserRepository.findByAccount(account);
        if (dbUser.isPresent() && dbUser.get().checkPassword(password)) {
            return;
        }
        throw new LoginException("잘못된 로그인입니다.");
    }

    public void loginByPost(Request request) {
        Map<String, String> queries = request.getRequestBody().queries();
        String account = queries.get(ACCOUNT);
        String password = queries.get(PASSWORD);
        Optional<User> dbUser = InMemoryUserRepository.findByAccount(account);
        if (dbUser.isPresent() && dbUser.get().checkPassword(password)) {
            return;
        }
        throw new LoginException("잘못된 로그인입니다.");
    }
}
