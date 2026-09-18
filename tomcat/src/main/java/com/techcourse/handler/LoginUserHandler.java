package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;
import org.apache.coyote.http.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginUserHandler implements ResourceHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginUserHandler.class);

    @Override
    public boolean canHandle(HttpServletRequest request) {
        return request.method() == HttpMethod.POST && request.path().equals("/login");
    }

    @Override
    public HttpServletResponse handle(HttpServletRequest request) {
        RequestBody body = request.body();

        Optional<String> account = body.get("account");
        Optional<String> password = body.get("password");

        if (account.isEmpty() || password.isEmpty()) {
            return HttpServletResponse.redirect("/401.html");
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account.get())
                .filter(found -> found.checkPassword(password.get()));

        if (user.isEmpty()) {
            return HttpServletResponse.redirect("/401.html");
        }
        log.info("login user: {}", user.get());

        return HttpServletResponse.redirect("/index.html")
                .addCookie("JSESSIONID", UUID.randomUUID().toString());
    }
}
