package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.request.Cookie;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.ResponseStatus;
import org.apache.exception.PageRedirectException;
import org.apache.front.AbstractController;
import org.apache.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginController extends AbstractController {

    public static final String ACCOUNT_KEY = "account";
    public static final String PASSWORD_KEY = "password";
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        login(request, response);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        if (request.hasQueryString()) {
            loginInConsole(request, response);
            return;
        }
        loginPage(request, response);
    }

    private void loginInConsole(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final String account = httpRequest.getQueryValueBy(ACCOUNT_KEY);
        final String password = httpRequest.getQueryValueBy(PASSWORD_KEY);

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(MemberNotFoundException::new);
        if (user.checkPassword(password)) {
            log.info("user : {}", user);
        }
        httpResponse.setViewPathAsBody(httpRequest.getPath());
    }

    private void login(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final String account = httpRequest.getBodyValue(ACCOUNT_KEY);
        final String password = httpRequest.getBodyValue(PASSWORD_KEY);
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new PageRedirectException.Unauthorized(httpResponse));

        if (user.checkPassword(password)) {
            final Session session = httpRequest.getSession(false);
            session.setAttribute("user", user);
            httpResponse.setStatus(ResponseStatus.MOVED_TEMP);
            httpResponse.setRedirect("/index.html");
            httpResponse.addCookie(Cookie.ofJSessionId(session.getId()));
            return;
        }
        throw new PageRedirectException.Unauthorized(httpResponse);
    }

    private void loginPage(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Session session = httpRequest.getSession(false);
        final Optional<Object> user = session.getAttribute("user");
        if (user.isPresent()) {
            httpResponse.setStatus(ResponseStatus.MOVED_TEMP);
            httpResponse.setRedirect("/index.html");
            return;
        }
        httpResponse.setViewPathAsBody(httpRequest.getPath());
    }
}
