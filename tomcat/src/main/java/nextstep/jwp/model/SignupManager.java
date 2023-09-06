package nextstep.jwp.model;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.RequestBody;

public class SignupManager {

    public static void singUp(final RequestBody requestBody) {
        final String account = requestBody.get("account");
        final String email = requestBody.get("email");
        final String password = requestBody.get("password");
        validate(requestBody);
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private static void validate(final RequestBody requestBody) {
        if (Objects.isNull(requestBody.get("account")) ||
                Objects.isNull(requestBody.get("email")) ||
                Objects.isNull(requestBody.get("password"))) {
            throw new IllegalArgumentException("회원가입 정보를 확인해주세요.");
        }
    }

    private SignupManager() {
    }
}
