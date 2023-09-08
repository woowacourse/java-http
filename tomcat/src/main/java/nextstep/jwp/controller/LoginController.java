package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.LoginManager;
import org.apache.coyote.http.common.ContentType;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.common.HttpHeader;
import org.apache.coyote.http.request.HttpCookie;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.QueryString;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;
import org.apache.coyote.http.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static nextstep.jwp.controller.Path.LOGIN;
import static nextstep.jwp.controller.Path.MAIN;
import static nextstep.jwp.controller.Path.UNAUTHORIZED;
import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http.common.HttpHeader.COOKIE;

public class LoginController extends RequestController {

    private static final String TARGET_URI = "login";
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String CREDENTIALS = "account";
    private static final String PASSWORD = "password";

    private final LoginManager loginManager;

    public LoginController(final LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.containsRequestUri(TARGET_URI);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        try {
            final User user = getValidateUser(request);

            log.info("로그인 성공! user = {}", user);
            final UUID uuid = createSessionToUser(user);

            setCookieAndRedirectToMain(response, uuid);
        } catch (final IllegalArgumentException e) {
            log.warn("login error = {}", e.getMessage());
            redirectToUnAuthorized(response);
        }
    }

    private static User getValidateUser(final HttpRequest request) {
        final Map<String, String> bodyParams = request.getParsedBody();
        final String account = bodyParams.get(CREDENTIALS);
        final String password = bodyParams.get(PASSWORD);

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 계정입니다. 다시 입력해주세요."));

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다. 다시 입력해주세요.");
        }

        return user;
    }

    private UUID createSessionToUser(final User user) {
        final UUID uuid = UUID.randomUUID();

        final Map<String, String> sessionData = Map.of(CREDENTIALS, user.getAccount());
        final Session session = new Session(uuid.toString());
        for (final Map.Entry<String, String> entry : sessionData.entrySet()) {
            session.add(entry.getKey(), entry.getValue());
        }

        loginManager.add(session);

        return uuid;
    }

    private static void setCookieAndRedirectToMain(final HttpResponse response, final UUID uuid) {
        response.changeStatusLine(StatusLine.from(StatusCode.FOUND));
        response.addHeader(HttpHeader.LOCATION, MAIN.getValue());
        response.addHeader(HttpHeader.SET_COOKIE, "JSESSIONID=" + uuid);
        response.changeBody(HttpBody.empty());
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        if (isAlreadyLogined(request)) {
            redirectToMain(response);
            return;
        }

        if (wantToLogin(request)) {
            doLoginProcess(request, response);
            return;
        }

        redirectToLoginPage(response);
    }

    private boolean isAlreadyLogined(final HttpRequest request) {
        if (!request.containsHeader(COOKIE)) {
            return false;
        }

        final HttpCookie cookies = HttpCookie.from(request.getHeader(COOKIE));

        return checkLogin(cookies.get("JSESSIONID"));
    }

    private void redirectToMain(final HttpResponse response) {
        response.mapToRedirect(MAIN.getValue());
    }

    private static boolean wantToLogin(final HttpRequest request) {
        return request.hasQueryString();
    }

    private void doLoginProcess(final HttpRequest request, final HttpResponse response) {
        final QueryString queryString = request.getQueryString();

        final String account = queryString.get(CREDENTIALS);
        final String password = queryString.get(PASSWORD);

        try {
            validateUserCredentials(account, password);
            log.info("로그인 성공! account = {}", account);
        } catch (final IllegalArgumentException e) {
            log.warn("login error = {}", e.getMessage());
            redirectToUnAuthorized(response);
            return;
        }

        redirectToMain(response);
    }

    private void redirectToUnAuthorized(final HttpResponse response) {
        response.mapToRedirect(UNAUTHORIZED.getValue());
    }

    private void validateUserCredentials(final String account, final String password) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 계정입니다. 다시 입력해주세요."));

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다. 다시 입력해주세요.");
        }
    }

    private boolean checkLogin(final String jSessionId) {
        return loginManager.isAlreadyLogined(jSessionId);
    }

    private void redirectToLoginPage(final HttpResponse response) throws IOException {
        response.changeStatusLine(StatusLine.from(StatusCode.OK));
        response.addHeader(CONTENT_TYPE, ContentType.HTML.getValue());
        response.changeBody(HttpBody.file(LOGIN.getValue()));
    }
}
