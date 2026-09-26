package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.SimpleSession;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public final class LoginController extends AbstractController {

    private final Manager sessionManager;

    public LoginController(Manager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        if (isLoggedIn(request)) {
            return HttpResponse.redirect(request.getVersion(), "/index.html");
        }

        return respondWithStaticResource(request, "/login.html");
    }

    private boolean isLoggedIn(HttpRequest request) {
        HttpSession session = request.getSession();
        return session != null && session.getAttribute("user") != null;
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        String account = request.getBodyParamValue("account");
        String password = request.getBodyParamValue("password");

        if (account == null || account.isBlank() || password == null || password.isBlank()) {
            return HttpResponse.badRequest(request.getVersion());
        }

        Optional<User> authenticatedUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (authenticatedUser.isEmpty()) {
            return respondUnauthorized(request);
        }

        HttpSession session = getOrCreateSession(request);
        session.setAttribute("user", authenticatedUser.get());

        return HttpResponse.redirect(request.getVersion(), "/index.html");
    }

    private HttpSession getOrCreateSession(HttpRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            return session;
        }

        SimpleSession newSession = SimpleSession.create();
        sessionManager.add(newSession);
        request.setSession(newSession);
        return newSession;
    }
}
