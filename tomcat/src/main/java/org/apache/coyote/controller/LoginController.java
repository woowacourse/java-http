package org.apache.coyote.controller;

import nextstep.FileResolver;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.domain.HttpSession;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.Controller;
import org.apache.coyote.controller.util.ResponseManager;
import org.apache.coyote.http11.domain.HttpRequest;
import org.apache.coyote.http11.util.HttpMethod;
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

    public LoginController(final SessionManager sessionManager) {
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

    private String getLoginPage(final HttpRequest request) throws IOException {
        if (request.containsCookie()) {
            return createRedirectResponse(ResponseManager.HTTP_302_FOUND.getHeader(), FileResolver.INDEX_HTML);
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
            log.info("queryStrings = ", session.getAttribute(USER_KEY));
            final String responseHeader = createJsessionResponseHeader(ResponseManager.HTTP_302_FOUND.getHeader(), session.getId());
            return createRedirectResponse(responseHeader, FileResolver.INDEX_HTML);
        }
        return createRedirectResponse(ResponseManager.HTTP_302_FOUND.getHeader(), FileResolver.HTML_401);
    }

    private boolean isValidUser(final User user, final String password) {
        return user.checkPassword(password);
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
