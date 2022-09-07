package nextstep.jwp.presentation.filter;

import org.apache.coyote.http11.Http11Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFilter {

    private static final Logger log = LoggerFactory.getLogger(LoginFilter.class);
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    public void doFilter(final Http11Request request) {
//        final Map<String, String> queryParams = request.getQueryParams();

//        final Optional<User> user = findByAccount(queryParams.get(ACCOUNT_KEY));
//        if (user.isPresent()) {
//            if (user.get().checkPassword(queryParams.get(PASSWORD_KEY))) {
//                log.info(user.get().toString());
//            }
//        }
    }

    public boolean support(final Http11Request request) {
//        final var requestLine = request.getRequestLine();
//        return requestLine.getRequestURI().contains("login") && !requestLine.getQueryParams().isEmpty();
        return false;
    }
}
