package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.HttpParser;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.status.HttpCookie;
import org.apache.coyote.status.Session;
import org.apache.coyote.status.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public String uri() {
        return "/login";
    }

    @Override
    public boolean support(final String uri, final String httpMethods) {
        return super.supportInternal(uri, httpMethods, this);
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        doPost(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> userMap = HttpParser.parseQueryString(request.getBody());
        final String account = userMap.get("account");
        final String password = userMap.get("password");
        final User foundUser = findUser(account);

        if (isLoginSuccess(foundUser, password)) {
            log.info("Login Success! {}", foundUser);
            loginSuccessEvent(foundUser, request, response);
            return;
        }

        loginFailEvent(request, response);
        log.info("Login Fail !");
    }

    private User findUser(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .stream()
                .findAny()
                .orElse(null);
    }

    private boolean isLoginSuccess(final User findUser, final String password) {
        return findUser != null && findUser.checkPassword(password);
    }

    private void loginSuccessEvent(final User user, final HttpRequest request, final HttpResponse response) {
        final Session session = request.getSession(true);
        session.setAttribute("user", user);
        SessionManager.add(session);

        response.sendRedirect("/index.html")
                .addCooke(HttpCookie.ofJSessionId(session.getId()));

        log.info("Redirect: /index.html");
    }

    private void loginFailEvent(final HttpRequest request, final HttpResponse response) {
        response.sendRedirect("/index.html");

        log.info("Redirect: /401.html");
    }
}
