package nextstep.jwp.controller;

import java.util.Optional;

import org.apache.catalina.handler.AbstractController;
import org.apache.catalina.handler.ResourceHandler;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final String DEFAULT_PATH = "/index";

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        if (isLogin(request)) {
            return getRedirectResponse(request, DEFAULT_PATH);
        }

        return ResourceHandler.render(request);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        if (isLogin(request)) {
            return getRedirectResponse(request, DEFAULT_PATH);
        }

        final String account = request.getQueryValue("account");
        final String password = request.getQueryValue("password");

        final Optional<User> foundUser = InMemoryUserRepository.findByAccountAndPassword(account, password);
        if (foundUser.isEmpty()) {
            return getRedirectResponse(request, "/401.html");
        }

        final User user = foundUser.get();
        final Session session = addSession(user);
        log.info("로그인 성공! 아이디: {}", user.getAccount());

        return new HttpResponse.Builder(request)
            .redirect().location(DEFAULT_PATH)
            .cookie(HttpCookie.fromJSession(session))
            .build();
    }

    private boolean isLogin(final HttpRequest request) {
        if (!request.hasSession()) {
            return false;
        }

        final Session session = request.getSession();
        if (!sessionManager.hasSession(session)) {
            return false;
        }

        return isSessionUserFound(session);
    }

    private boolean isSessionUserFound(final Session session) {
        final Session foundSession = sessionManager.findSession(session.getId());
        final User user = getUser(foundSession);
        return InMemoryUserRepository.findByAccount(user.getAccount())
            .isPresent();
    }

    private User getUser(final Session session) {
        return (User)session.getAttribute("user");
    }

    private HttpResponse getRedirectResponse(final HttpRequest request, final String location) {
        return new HttpResponse.Builder(request)
            .redirect()
            .location(location)
            .build();
    }

    private Session addSession(final User user) {
        final String jSessionId = new HttpCookie().getCookieValue("JSESSIONID");
        final Session session = new Session(jSessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);
        return session;
    }
}
