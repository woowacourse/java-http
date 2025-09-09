package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.contoller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.domain.HttpMethod;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void registerCommands() {
        this.addCommand(HttpMethod.GET, this::getToLoginPage);
        this.addCommand(HttpMethod.POST, this::postToLogin);
    }

    public String getToLoginPage(final Http11Request request, final Http11Response response) {
        if (isSessionContinued(request)) {
            return handleLoginSuccess(response);
        }
        return "/login";
    }

    public String postToLogin(final Http11Request request, final Http11Response response) {
        final String account = request.body().getValueByKey("account");
        final String password = request.body().getValueByKey("password");
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("User found: {}", user.get());
            return handleSessionCreation(user.get(), response);
        }
        throw new UnauthorizedException(response);
    }

    private boolean isSessionContinued(final Http11Request request) {
        final SessionManager sessionManager = SessionManager.getInstance();
        return !request.isCookiesEmpty() && sessionManager.findSession(request.getJsessionid()) != null;
    }

    private String handleSessionCreation(final User user, final Http11Response response) {
        final Session session = new Session();
        session.setAttribute(user.getClass().getName(), user);

        final SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        response.addCookie("JSESSIONID", session.getId());

        return handleLoginSuccess(response);
    }

    private String handleLoginSuccess(Http11Response response) {
        response.setState(HttpStatus.Found);
        return "/index";
    }
}
