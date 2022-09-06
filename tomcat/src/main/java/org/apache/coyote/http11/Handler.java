package org.apache.coyote.http11;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.LoggerFactory;

public class Handler {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Handler.class);

    private final HttpRequestStartLine request;

    public Handler(HttpRequestStartLine request) {
        this.request = request;
    }

    public void handle() {
        if (request.getMethod().equals("GET") && request.getUri().equals("/login")) {
            checkUser();
        }
    }

    private void checkUser() {
        final Map<String, String> queryParams = request.getQueryParams();
        final Optional<User> user = InMemoryUserRepository.findByAccount(queryParams.get("account"));

        if (user.isPresent()) {
            final User loginUser = user.get();
            logLoginUser(queryParams, loginUser);
        }
    }

    private void logLoginUser(Map<String, String> queryParams, User loginUser) {
        if (loginUser.checkPassword(queryParams.get("password"))) {
            log.info("user : " + loginUser);
        }
    }
}
