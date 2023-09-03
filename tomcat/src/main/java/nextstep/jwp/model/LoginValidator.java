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

    public static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final Logger log = LoggerFactory.getLogger(LoginValidator.class);

    public static boolean check(final HttpRequest request, final String sessionId) {
        final User user = getUser(sessionId);
        if (Objects.isNull(user)) {
            return newLoginCheck(request, sessionId);
        }
        log.info("user = {}", user);
        return true;
    }

    private static boolean newLoginCheck(final HttpRequest request, final String sessionId) {
        final RequestBody requestBody = request.getRequestBody();
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");

        validate(account, password);

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("계정을 확인해주세요."));
        if (user.checkPassword(password)) {
            log.info("user = {}", user);
            final Session session = SESSION_MANAGER.findSession(sessionId);
            session.setAttribute("user", user);
            return true;
        }
        return false;
    }

    private static User getUser(final String sessionId) {
        final Session session = SESSION_MANAGER.findSession(sessionId);
        return (User) session.getAttribute("user");
    }

    private static void validate(final String account, final String password) {
        if (Objects.isNull(account) || Objects.isNull(password)) {
            throw new IllegalArgumentException("계정 혹은 비밀번호를 확인해주세요.");
        }
    }

    private LoginValidator() {
    }
}
