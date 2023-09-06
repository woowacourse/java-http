package nextstep.jwp.model;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.RequestBody;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginValidator {

    private static final Logger log = LoggerFactory.getLogger(LoginValidator.class);

    public static boolean check(final HttpRequest request, final Session session) {
        final RequestBody requestBody = request.getRequestBody();
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");

        validate(account, password);

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("계정을 확인해주세요."));
        if (user.checkPassword(password)) {
            log.info("user = {}", user);
            addSessionBySuccessLogin(user, session);
            return true;
        }
        return false;
    }

    private static void addSessionBySuccessLogin(final User user, final Session session) {
        session.setAttribute("user", user);
        final SessionManager sessionManager = new SessionManager();
        sessionManager.add(session);
    }

    private static void validate(final String account, final String password) {
        if (Objects.isNull(account) || Objects.isNull(password)) {
            throw new IllegalArgumentException("계정 혹은 비밀번호를 확인해주세요.");
        }
    }

    private LoginValidator() {
    }
}
