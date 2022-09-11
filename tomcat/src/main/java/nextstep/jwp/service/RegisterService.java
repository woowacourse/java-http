package nextstep.jwp.service;

import java.util.Map;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterService {

    public static void register(Map<String, String> requestBody) {
        validRequestBody(requestBody);
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        String email = requestBody.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private static void validRequestBody(Map<String, String> requestBody) {
        validAccount(requestBody);
        validPassword(requestBody);
        validEmail(requestBody);
    }

    private static void validAccount(Map<String, String> requestBody) {
        if (!requestBody.containsKey("account") || requestBody.get("account").isBlank()) {
            throw new NoSuchElementException("account를 입력해주세요");
        }
    }

    private static void validPassword(Map<String, String> requestBody) {
        if (!requestBody.containsKey("password") || requestBody.get("password").isBlank()) {
            throw new NoSuchElementException("password를 입력해주세요");
        }
    }

    private static void validEmail(Map<String, String> requestBody) {
        if (!requestBody.containsKey("email") || requestBody.get("email").isBlank()) {
            throw new NoSuchElementException("email을 입력해주세요");
        }
    }

    private RegisterService() {
    }
}
