package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseConfigurator;
import org.apache.coyote.http11.session.CookieSession;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.session.SessionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final UserService userService;
    private final SessionManager sessionManager;

    public LoginController() {
        this.userService = new UserService();
        this.sessionManager = SessionManager.getInstance();
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final String account = request.getBodyParameter("account");
        final String password = request.getBodyParameter("password");
        final Optional<User> user = userService.login(account, password);

        if (user.isEmpty()) {
            HttpResponseConfigurator.unauthorized(response);
            return;
        }

        log.info("user: " + user.get());
        final CookieSession session = CookieSession.fromValues(Map.of("user", user.get()));
        sessionManager.add(session);
        final HttpCookie cookie = session.createCookie();

        HttpResponseConfigurator.redirect(response, "/index.html");
        response.setCookie(cookie);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Optional<Session> session = SessionParser.extractCookieSessionFromRequest(request);
        if (session.isPresent()) {
            final User loginUser = (User) session.get().getAttribute("user");
            if (loginUser != null) {
                HttpResponseConfigurator.redirect(response, "/index.html");
                return;
            }
        }
        HttpResponseConfigurator.okWithStaticResource(response, "/login.html");
    }

    @Override
    public boolean support(final HttpRequest request) {
        return request.isPathEqualsTo("/login");
    }
}
