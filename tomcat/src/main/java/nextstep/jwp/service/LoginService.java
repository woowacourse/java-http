package nextstep.jwp.service;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.RequestBody;

public class LoginService {

    public User login(final HttpRequest request) {
        final RequestBody requestBody = request.getRequestBody();
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");

        validate(account, password);

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("계정을 확인해주세요."));
        if (user.checkPassword(password)) {
            return user;
        }

        throw new IllegalArgumentException("비밀번호를 확인해주세요.");
    }

    private static void validate(final String account, final String password) {
        if (Objects.isNull(account) || Objects.isNull(password)) {
            throw new IllegalArgumentException("계정 혹은 비밀번호를 확인해주세요.");
        }
    }

    private LoginService() {
    }

    public static LoginService getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        public static final LoginService instance = new LoginService();
    }
}
