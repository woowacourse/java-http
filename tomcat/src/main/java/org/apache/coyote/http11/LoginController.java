package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_SUCCESS = "/index.html";
    private static final String LOGIN_FAILURE = "/401.html";
    private static final String USER = "user";
    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

    private final StaticResourceController staticResourceController = new StaticResourceController();

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String sessionId = request.getCookie(JSESSIONID);
        final Session session = SESSION_MANAGER.findSession(sessionId);
        if (isLoggedIn(session)) {
            setRedirectResponse(response, LOGIN_SUCCESS, "");
            return;
        }
        staticResourceController.service(request, response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String sessionId = request.getCookie(JSESSIONID);
        final Optional<User> authenticatedUser = authenticate(request.getBody());
        if (authenticatedUser.isEmpty()) {
            setRedirectResponse(response, LOGIN_FAILURE, createSetCookieHeader(sessionId));
            return;
        }

        final User user = authenticatedUser.orElseThrow();
        final Session loginSession = getOrCreateSession(sessionId);
        loginSession.setAttribute(USER, user);
        final String responseCookie = createSessionCookie(sessionId, loginSession);
        setRedirectResponse(response, LOGIN_SUCCESS, responseCookie);
    }

    private Session getOrCreateSession(final String sessionId) {
        final Session session = SESSION_MANAGER.findSession(sessionId);
        if (session != null) {
            return session;
        }
        return SESSION_MANAGER.createSession();
    }

    private String createSessionCookie(final String requestSessionId, final Session session) {
        if (requestSessionId != null && requestSessionId.equals(session.getId())) {
            return "";
        }
        return JSESSIONID + "=" + session.getId();
    }

    private boolean isLoggedIn(final Session session) {
        return getUser(session) != null;
    }

    private User getUser(final Session session) {
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(USER);
    }

    private Optional<User> authenticate(final String requestBody) {
        final Map<String, String> formData = parseFormData(requestBody);
        final String account = formData.get("account");
        final String password = formData.get("password");
        if (account == null || password == null) {
            return Optional.empty();
        }

        final Optional<User> authenticatedUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
        authenticatedUser.ifPresent(user -> log.info("로그인 성공: {}", user));
        return authenticatedUser;
    }

    private Map<String, String> parseFormData(final String requestBody) {
        final Map<String, String> formData = new HashMap<>();
        final String[] formFields = requestBody.split("&");

        for (String formField : formFields) {
            addFormField(formData, formField);
        }
        return formData;
    }

    private void addFormField(final Map<String, String> formData, final String formField) {
        final String[] keyValue = formField.split("=", 2);
        if (keyValue.length < 2) {
            return;
        }
        formData.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
    }
}
