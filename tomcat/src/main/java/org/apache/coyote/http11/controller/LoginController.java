package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.SessionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String USER_SESSION_ATTRIBUTE = "user";

    private final SessionResolver sessionResolver;
    private final SessionManager sessionManager;
    private final StaticResourceController staticResourceController;

    public LoginController(final SessionResolver sessionResolver, final SessionManager sessionManager,
                           final StaticResourceController staticResourceController) {
        this.sessionResolver = sessionResolver;
        this.sessionManager = sessionManager;
        this.staticResourceController = staticResourceController;
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        Map<String, String> params = request.params();
        String account = params.get("account");
        String password = params.get("password");

        Optional<User> authenticatedUser = authenticate(account, password);
        if (authenticatedUser.isEmpty()) {
            return HttpResponse.redirect("/401.html", null);
        }
        User user = authenticatedUser.get();
        String sessionId = addUserToSession(request, user);

        log.info("login user: {}", user);
        return HttpResponse.redirect("/index.html", sessionId);
    }

    private Optional<User> authenticate(final String account, final String password) {
        if (account == null || password == null) {
            return Optional.empty();
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            log.info("account doesn't exist: {}", account);
            return Optional.empty();
        }
        return user.filter(foundUser -> foundUser.checkPassword(password));
    }

    private String addUserToSession(final HttpRequest request, final User user) {
        Session session = sessionResolver.resolve(request);
        if (session != null) {
            session.setAttribute(USER_SESSION_ATTRIBUTE, user);
            return null;
        }
        Session newSession = sessionManager.createSession();
        newSession.setAttribute(USER_SESSION_ATTRIBUTE, user);
        return newSession.getId();
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        Session session = sessionResolver.resolve(request);
        if (getUser(session) != null) {
            return HttpResponse.redirect("/index.html", null);
        }
        return staticResourceController.serveResource(request);
    }

    private User getUser(final Session session) {
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(USER_SESSION_ATTRIBUTE);
    }
}
