package nextstep.jwp.controller;

import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.apache.coyote.http11.request.HttpMethod.POST;
import static org.apache.coyote.http11.response.header.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.util.StringUtils.HTML_FILE_EXTENSION;

import java.io.IOException;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.Cookie;
import org.apache.coyote.http11.response.header.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String PATH = "/login";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String LOGIN_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String LOGIN_FAILURE_REDIRECT_URI = "http://localhost:8080/401.html";
    private static final String JSESSION_COOKIE_KEY = "JSESSIONID";
    private static final String USER_SESSION_NAME = "user";

    private final Manager sessionManager;

    public LoginController(Manager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return httpRequest.hasRequestPathOf(PATH) &&
                (httpRequest.hasHttpMethodOf(GET) || httpRequest.hasHttpMethodOf(POST));
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (isLoginPageRequest(httpRequest) && hasAlreadyLogin(httpRequest)) {
            redirectToIndexHtml(httpResponse);
            return;
        }
        super.readFile(PATH + HTML_FILE_EXTENSION, httpResponse);
    }

    private boolean isLoginPageRequest(HttpRequest httpRequest) {
        return httpRequest.hasRequestPathOf(PATH) && httpRequest.hasHttpMethodOf(GET);
    }

    private boolean hasAlreadyLogin(HttpRequest httpRequest) throws IOException {
        return httpRequest.hasCookieOf(JSESSION_COOKIE_KEY) &&
                sessionManager.findSession(httpRequest.getCookieOf(JSESSION_COOKIE_KEY)) != null;
    }

    private void redirectToIndexHtml(HttpResponse httpResponse) {
        httpResponse.setHttpStatusCode(FOUND);
        httpResponse.addHeader(new Location(LOGIN_SUCCESS_REDIRECT_URI));
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setHttpStatusCode(FOUND);
        String account = httpRequest.getParamValueOf(ACCOUNT_KEY);
        String password = httpRequest.getParamValueOf(PASSWORD_KEY);
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (isInvalidUserInfo(user, password)) {
            httpResponse.addHeader(new Location(LOGIN_FAILURE_REDIRECT_URI));
            return;
        }
        serviceLoginSuccess(httpResponse, user);
        log.info(user.get().toString());
    }

    private boolean isInvalidUserInfo(Optional<User> user, String password) {
        return user.isEmpty() ||
                !user.get()
                        .checkPassword(password);
    }

    private void serviceLoginSuccess(HttpResponse httpResponse, Optional<User> user) {
        httpResponse.addHeader(new Location(LOGIN_SUCCESS_REDIRECT_URI));
        Session session = new Session(USER_SESSION_NAME, user.get());
        sessionManager.add(session);
        httpResponse.addHeader(Cookie.fromResponse(JSESSION_COOKIE_KEY, session.getId()));
    }
}
