package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpServletRequest;
import org.apache.coyote.http.HttpServletResponse;
import org.apache.coyote.http.RequestBody;

public class RegisterUserHandler implements ResourceHandler {

    @Override
    public boolean canHandle(HttpServletRequest request) {
        return request.method() == HttpMethod.POST && request.path().equals("/register");
    }

    @Override
    public HttpServletResponse handle(HttpServletRequest request) {
        RequestBody body = request.body();

        Optional<String> account = body.get("account");
        Optional<String> password = body.get("password");
        Optional<String> email = body.get("email");

        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            return HttpServletResponse.redirect("/register");
        }

        if (InMemoryUserRepository.findByAccount(account.get()).isPresent()) {
            return HttpServletResponse.redirect("/register");
        }

        User user = new User(account.get(), password.get(), email.get());
        InMemoryUserRepository.save(user);

        return HttpServletResponse.redirect("/index.html");
    }
}
