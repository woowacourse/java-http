package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.controller.AbstrcatController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.catalina.session.Session.JSESSIONID_COOKIE_NAME;

public class RegisterController extends AbstrcatController {

    private static final String MAPPED_URL = "/register";

    private static final String LOGIN_USER_REDIRECT_PAGE = "index.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final Session session = SessionManager.findSession(request.getCookie(JSESSIONID_COOKIE_NAME));

        if (session != null && session.getUser() instanceof User) {
            response.redirectTo(LOGIN_USER_REDIRECT_PAGE);
        }
        response.hostingPage(REGISTER_PAGE);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final Session session = SessionManager.findSession(request.getCookie(JSESSIONID_COOKIE_NAME));

        final User registeredUser = processRegister(request);
        session.setUser(registeredUser);
        response.redirectTo(LOGIN_USER_REDIRECT_PAGE);
    }

    private User processRegister(final HttpRequest request) {
        if (!request.containsBody(ACCOUNT_KEY) || !request.containsBody(PASSWORD_KEY) || !request.containsBody(EMAIL_KEY)) {
            return null;
        }
        final String account = request.getBody(ACCOUNT_KEY);
        final String password = request.getBody(PASSWORD_KEY);
        final String email = request.getBody(EMAIL_KEY);

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return null;
        }
        final User registeredUser = new User(account, password, email);
        InMemoryUserRepository.save(registeredUser);
        return registeredUser;
    }

    @Override
    public boolean isMappedController(HttpRequest request) {
        return MAPPED_URL.equals(request.getPath());
    }
}
