package nextstep.jwp.presentation.filter;

import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFilter {

    private static final Logger log = LoggerFactory.getLogger(LoginFilter.class);
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    public void doFilter(final Request request) {
        final Map<String, String> queryParams = request.getQueryParams();

        final Optional<User> user = findByAccount(queryParams.get(ACCOUNT_KEY));
        if (user.isPresent()) {
            if (user.get().checkPassword(queryParams.get(PASSWORD_KEY))) {
                log.info(user.get().toString());
            }
        }
    }

    public boolean support(final Request request) {
        return request.getRequestURI().contains("login") && !request.getQueryParams().isEmpty();
    }
}
