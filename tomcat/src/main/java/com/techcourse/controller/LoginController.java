package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.LoginRequest;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.StatusLine;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final LoginRequest loginRequest = LoginRequest.from(request.requestBody());
        final Optional<User> filteredUser = InMemoryUserRepository.findByAccount(loginRequest.account())
            .filter(foundUser -> foundUser.checkPassword(loginRequest.password()));

        if (filteredUser.isPresent()) {
            final User user = filteredUser.get();
            final Session session = request.getSession();
            log.info("user: {}", user);
            session.addAttribute("user", user);
            response.sendRedirect(HttpStatus.FOUND, "/index");
            return;
        }

        response.forward(HttpStatus.UNAUTHORIZED, "/401.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final Session session = request.getSession(false);
        if (session != null && session.hasAttribute("user")) {
            response.sendRedirect(HttpStatus.FOUND, "/index");
            return;
        }

        response.forward(HttpStatus.OK, "/login.html");
    }
}
