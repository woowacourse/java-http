package com.techcourse.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.DispatchResult;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.WebController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.LoginRequest;
import org.apache.coyote.http11.Session;

@WebController(path = "/login")
public class LoginController extends AbstractController {

    @Override
    protected DispatchResult doPost(HttpRequest request, HttpResponse response) throws Exception {
        final LoginRequest loginRequest = LoginRequest.from(request.requestBody());
        final Optional<User> filteredUser = InMemoryUserRepository.findByAccount(loginRequest.account())
            .filter(foundUser -> foundUser.checkPassword(loginRequest.password()));

        if (filteredUser.isEmpty()) {
            return DispatchResult.redirect(HttpStatus.FOUND, "/401.html");
        }

        final Session session = SessionManager.getInstance()
            .createNewSession();
        log.info("user: {}", filteredUser.get());
        session.addAttribute("user", filteredUser.get());
        response.addHeader("Set-Cookie", "JSESSIONID=" + session.id());

        return DispatchResult.redirect(HttpStatus.FOUND, "/index");
    }

    @Override
    protected DispatchResult doGet(HttpRequest request, HttpResponse response) throws Exception {
        final HttpCookie cookie = request.cookie();
        final Session session = SessionManager.getInstance()
            .findSession(cookie.getValue("JSESSIONID"));

        if (session != null && session.hasAttribute("user")) {
            return DispatchResult.redirect(HttpStatus.FOUND, "/index");
        }

        return DispatchResult.forward(HttpStatus.OK, "/login.html");
    }
}
