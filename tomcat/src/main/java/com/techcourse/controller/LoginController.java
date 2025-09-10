package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnauthorizedException;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.catalina.controller.AbstractController;
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
    private static final String USER_SESSION_KEY = "USER";
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    protected void registerCommands() {
        this.addCommand(HttpMethod.GET, this::getToLoginPage);
        this.addCommand(HttpMethod.POST, this::postToLogin);
    }

    public String getToLoginPage(final Http11Request request, final Http11Response response) {
        if (isSessionValid(request)) {
            return handleLoginSuccess(response);
        }
        return "/login";
    }

    public String postToLogin(final Http11Request request, final Http11Response response) {
        final String account = request.body().getValueByKey("account");
        final String password = request.body().getValueByKey("password");

        final User user = findUser(account, password, response);
        log.info("User authenticated: {}", user);
        createSession(user, response);
        return handleLoginSuccess(response);
    }

    private boolean isSessionValid(final Http11Request request) {
        final Manager manager = SessionManager.getInstance();
        if (request.isCookiesEmpty()) {
            return false;
        }

        final String sessionId = request.getJsessionid();
        if (sessionId == null) {
            return false;
        }

        final Session session = manager.findSession(sessionId);
        return session != null && session.getAttribute(USER_SESSION_KEY) != null;
    }

    private String handleLoginSuccess(final Http11Response response) {
        response.setState(HttpStatus.Found);
        return "/index";
    }

    private User findUser(final String account, final String password, final Http11Response response) {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            return user.get();
        }
        throw new UnauthorizedException(response);
    }

    private void createSession(final User user, final Http11Response response) {
        final Manager manager = SessionManager.getInstance();
        final Session session = new Session();
        session.setAttribute(USER_SESSION_KEY, user);
        manager.add(session);
        response.addCookie(JSESSIONID, session.getId());
    }
}
