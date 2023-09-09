package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.controller.AbstrcatController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.catalina.session.Session.JSESSIONID_COOKIE_NAME;

public class LoginController extends AbstrcatController {

    private static final String MAPPED_URL = "/login";
    private static final String LOGIN_USER_REDIRECT_PAGE = "index.html";
    private static final String LOGIN_PAGE = "login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Session session = SessionManager.findSession(request.getCookie(JSESSIONID_COOKIE_NAME));

        if (session != null && session.getUser() instanceof User) {
            response.redirectTo(LOGIN_USER_REDIRECT_PAGE);
            return;
        }
        response.hostingPage(LOGIN_PAGE);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Session session = SessionManager.findSession(request.getCookie(JSESSIONID_COOKIE_NAME));

        if (processLogin(request)) {
            final User user = InMemoryUserRepository.findByAccount(request.getBody(ACCOUNT_KEY)).get();
            session.setUser(user);
            response.redirectTo(LOGIN_USER_REDIRECT_PAGE);
            return;
        }
        response.redirectTo(UNAUTHORIZED_PAGE);
    }

    private boolean processLogin(final HttpRequest request) {
        if (!request.containsBody(ACCOUNT_KEY) || !request.containsBody(PASSWORD_KEY)) {
            return false;
        }
        final String account = request.getBody(ACCOUNT_KEY);
        final String password = request.getBody(PASSWORD_KEY);

        return InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }

    @Override
    public boolean isMappedController(HttpRequest request) {
        return MAPPED_URL.equals(request.getPath());
    }
}
