package nextstep.jwp.service;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.RegisterException;
import nextstep.jwp.model.Request;
import nextstep.jwp.model.User;

public class RegisterService {

    private static final String ACCOUNT = "account";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    public void register(Request request) {
        Map<String, String> queries = request.getRequestBody().queries();
        String account = queries.get(ACCOUNT);
        String email = queries.get(EMAIL);
        String password = queries.get(PASSWORD);
        if (InMemoryUserRepository.existsByAccount(account)
                || InMemoryUserRepository.existsByEmail(email)) {
            throw new RegisterException("잘못된 회원가입입니다.");
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }
}
