package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.SessionSupport;
import org.apache.coyote.http11.util.StaticResourceResolver;

public class LoginController extends AbstractController {

    private final Manager manager;

    public LoginController(Manager manager) {
        this.manager = manager;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response, HttpSession session) throws Exception {
        if (session.getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }

        final var responseBody = StaticResourceResolver.read(request.getPath());
        response.sendOk("text/html", responseBody);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response, HttpSession session) throws Exception {
        final var account = request.getParameter("account");
        final var password = request.getParameter("password");
        final Optional<User> optionalUser = findUserByAccount(account);

        if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
            SessionSupport.rotateSessionAfterLogin(manager, session, optionalUser.get(), response);
            response.sendRedirect("/index.html");
            return;
        }

        response.sendRedirect("/401.html");
    }

    private Optional<User> findUserByAccount(final String account) {
        if (account == null || account.isBlank()) {
            return Optional.empty();
        }
        return InMemoryUserRepository.findByAccount(account);
    }
}
