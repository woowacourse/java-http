package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;
import org.apache.coyote.http.QueryParam;
import org.apache.coyote.http.StaticResourceBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements ResourceHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public boolean canHandle(HttpServletRequest request) {
        return request.path().equals("/login");
    }

    @Override
    public HttpServletResponse handle(HttpServletRequest request) throws IOException {
        logIfLoginSucceeds(request.requestLine().getQueryParam());
        return HttpServletResponse.ok(StaticResourceBody.from(request.path()));
    }

    private void logIfLoginSucceeds(QueryParam queryParam) {
        final Optional<String> account = queryParam.get("account");
        final Optional<String> password = queryParam.get("password");
        if (account.isEmpty() || password.isEmpty()) {
            return;
        }

        InMemoryUserRepository.findByAccount(account.get())
                .filter(user -> user.checkPassword(password.get()))
                .ifPresent(user -> log.info("user : {}", user));
    }
}
