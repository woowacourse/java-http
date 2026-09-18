package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
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
        QueryParam queryParam = request.requestLine().getQueryParam();
        if(queryParam.isEmpty()) {
            return HttpServletResponse.ok(StaticResourceBody.from("/login"));
        }

        Optional<User> user = login(queryParam);
        if (user.isEmpty()) {
            return HttpServletResponse.redirect("401.html");
        }
        log.info("login user: {}", user);
        return HttpServletResponse.redirect("/index.html");
    }

    private Optional<User> login(QueryParam queryParam) {
        final Optional<String> account = queryParam.get("account");
        final Optional<String> password = queryParam.get("password");
        if (account.isEmpty() || password.isEmpty()) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account.get())
                .filter(user -> user.checkPassword(password.get()));
    }
}
