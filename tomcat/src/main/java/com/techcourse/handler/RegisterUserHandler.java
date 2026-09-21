package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.RequestBody;

public class RegisterUserHandler implements ResourceHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == HttpMethod.POST && request.path().equals("/register");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        RequestBody body = request.body();

        Optional<String> account = body.get("account");
        Optional<String> password = body.get("password");
        Optional<String> email = body.get("email");

        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            return HttpResponse.redirect("/register");
        }

        if (InMemoryUserRepository.findByAccount(account.get()).isPresent()) {
            return HttpResponse.redirect("/register");
        }

        User user = new User(account.get(), password.get(), email.get());
        InMemoryUserRepository.save(user);

        return HttpResponse.redirect("/index.html");
    }
}
