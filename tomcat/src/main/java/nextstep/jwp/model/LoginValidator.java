package nextstep.jwp.model;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginValidator {

    private static final Logger log = LoggerFactory.getLogger(LoginValidator.class);

    public static void check(final String requestURI, final QueryString queryStrings) {
        if (isNotLoginRequest(requestURI) || queryStrings.isEmpty()) {
            return;
        }
        final String account = queryStrings.get("account");
        final String password = queryStrings.get("password");

        validate(account, password);

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("계정을 확인해주세요."));
        if (user.checkPassword(password)) {
            log.info("user = {}", user);
        }
    }

    private static boolean isNotLoginRequest(final String requestURI) {
        return !requestURI.contains("login") || !requestURI.contains("?");
    }

    private static void validate(final String account, final String password) {
        if (Objects.isNull(account) || Objects.isNull(password)) {
            throw new IllegalArgumentException("계정 혹은 비밀번호를 확인해주세요.");
        }
    }
}
