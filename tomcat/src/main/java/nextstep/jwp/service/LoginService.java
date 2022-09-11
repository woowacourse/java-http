package nextstep.jwp.service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginService {

    public static User login(Map<String, String> requestBody) {
        validRequestBody(requestBody);

        User user = findUser(requestBody.get("account"));
        if (user.checkPassword(requestBody.get("password"))) {
            return user;
        }
        throw new IllegalArgumentException("password가 일치하지 않습니다.");
    }

    private static User findUser(String account) {
        Optional<User> byAccount = InMemoryUserRepository.findByAccount(account);
        if (byAccount.isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 account입니다.");
        }
        return byAccount.get();
    }

    private static void validRequestBody(Map<String, String> requestBody) {
        if (requestBody.get("account").isBlank()) {
            throw new NoSuchElementException("account를 입력해주세요");
        }
        if (requestBody.get("password").isBlank()) {
            throw new NoSuchElementException("password를 입력해주세요");
        }
    }
}
