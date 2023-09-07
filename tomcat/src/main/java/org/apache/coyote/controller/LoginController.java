package org.apache.coyote.controller;

import nextstep.FileResolver;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Controller;
import org.apache.coyote.controller.util.HttpResponse;
import org.apache.coyote.handler.SessionManager;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpSession;
import org.apache.coyote.http11.http.util.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class LoginController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String SET_COOKIE_PREFIX = "Set-Cookie: ";
    private static final String SESSION_NAME = "JSESSIONID";
    private static final String SESSION_DELIMITER = "=";
    private static final String USER_KEY = "user";
    private static final String PASSWORD = "password";

    private final SessionManager sessionManager;
    private static final LoginController loginController = new LoginController(new SessionManager());

    private LoginController(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public String run(final HttpRequest request) throws IOException {
        final String parsedUri = request.getUri();
        final String method = request.getMethod();
        if (HttpMethod.GET.isSameMethod(method)) {
            return getLoginPage(request);
        }
        if (HttpMethod.POST.isSameMethod(method)) {
            return login(request);
        }
        final FileResolver file = FileResolver.findFile(parsedUri);
        return file.createResponse();
    }

    @Override
    public String getResponse(final String uri) {
        return null;
    }

    public static LoginController getController() {
        return loginController;
    }

    private String getLoginPage(final HttpRequest request) throws IOException {
        if (request.containsCookie()) {
            return createRedirectResponse(HttpResponse.HTTP_302_FOUND.getValue(), FileResolver.INDEX_HTML);
        }
        return FileResolver.LOGIN.createResponse();
    }

    private String login(final HttpRequest request) {
        final Map<String, String> body = request.getBody();
        if (request.containsCookie()) {
            final String sessionId = request.getCookie().getValue(SESSION_NAME);
            final HttpSession session = sessionManager.findSession(sessionId);
            final User user = (User) session.getAttribute(USER_KEY);
            final boolean validUser = isValidUser(user, body.get(PASSWORD));
            return createResponse(validUser, session);
        }
        final User user = InMemoryUserRepository.findByAccount(body.get("account"))
                                                .orElseThrow(() -> new IllegalArgumentException("잘못된 유저 정보입니다."));
        final HttpSession newSession = sessionManager.createSession(user);
        return createResponse(true, newSession);
    }

    private String createResponse(final boolean validUser, final HttpSession session) {
        if (validUser) {
            final String responseHeader = createJsessionResponseHeader(HttpResponse.HTTP_302_FOUND.getValue(), session.getId());
            return createRedirectResponse(responseHeader, FileResolver.INDEX_HTML);
        }
        return createRedirectResponse(HttpResponse.HTTP_302_FOUND.getValue(), FileResolver.HTML_401);
    }

    private boolean isValidUser(final User user, final String password) {
        if (user.checkPassword(password)) {
            log.info("로그인 성공!\n회원 아이디: {} ", user.getAccount());
            return true;
        }
        return false;
    }

    private String createJsessionResponseHeader(final String responseHeader, final String session) {
        return String.join("\r\n",
                responseHeader,
                SET_COOKIE_PREFIX + SESSION_NAME + SESSION_DELIMITER + session
        );
    }

    private String createRedirectResponse(final String responseHeader, final FileResolver file) {
        return String.join("\r\n",
                responseHeader,
                "Location: " + file.getFileName() + " ",
                ""
        );
    }
}
