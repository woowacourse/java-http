package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginUserHandler implements ResourceHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginUserHandler.class);

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == HttpMethod.POST && request.path().equals("/login");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        RequestBody body = request.body();

        Optional<String> account = body.get("account");
        Optional<String> password = body.get("password");

        if (account.isEmpty() || password.isEmpty()) {
            return HttpResponse.redirect("/401.html");
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account.get())
                .filter(found -> found.checkPassword(password.get()));

        if (user.isEmpty()) {
            return HttpResponse.redirect("/401.html");
        }
        log.info("login user: {}", user.get());

        Session session = request.getSession(true);
        session.setAttribute("user", user.get());
        return HttpResponse.redirect("/index.html")
                .addCookie("JSESSIONID", session.getId());
    }
}
