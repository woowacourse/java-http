package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class LoginController implements Controller {

    @Override
    public HttpResponse handle(HttpRequest request) {
        Map<String, String> formData = request.body().parseFormData();
        String account = formData.get("account");
        String password = formData.get("password");

        Optional<User> authenticatedUser = authenticate(account, password);

        if (authenticatedUser.isEmpty()) {
            return HttpResponse.redirect("/401.html");
        }

        Session session = createSession(authenticatedUser);

        return HttpResponse.redirect("/index.html", new Cookie("JSESSIONID", session.getId()));
    }

    @Nonnull
    private static Session createSession(Optional<User> authenticatedUser) {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        session.setAttribute("user", authenticatedUser.get());
        SessionManager.add(session);

        return session;
    }

    private Optional<User> authenticate(String account, String password) {
        if (account == null || account.isBlank()) {
            return Optional.empty();
        }

        if (password == null || password.isBlank()) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}
