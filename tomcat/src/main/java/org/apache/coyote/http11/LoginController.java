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
        final String setCookie = createSetCookieHeader(sessionId);
        final String location = getLoginRedirectionLocation(request.getBody());
        String responseCookie = setCookie;

        if (location.equals(LOGIN_SUCCESS)) {
            final User user = findUser(request.getBody());
            final Session loginSession = getOrCreateSession(sessionId);
            loginSession.setAttribute(USER, user);
            responseCookie = createSessionCookie(sessionId, loginSession);
        }

        setRedirectResponse(response, location, responseCookie);
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

    private String getLoginRedirectionLocation(final String requestBody) {
        final String[] formParts = requestBody.split("&");
        if (checkUser(formParts)) {
            return LOGIN_SUCCESS;
        }
        return LOGIN_FAILURE;
    }

    private boolean checkUser(final String[] requestParts) {
        if (requestParts.length < 2) {
            return false;
        }

        final String[] accountPart = requestParts[0].split("=", 2);
        final String[] passwordPart = requestParts[1].split("=", 2);
        if (accountPart.length < 2 || passwordPart.length < 2) {
            return false;
        }

        final String account = accountPart[1];
        final String password = passwordPart[1];
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return false;
        }
        if (user.get().checkPassword(password)) {
            log.info("로그인 성공: {}", user.get());
            return true;
        }
        return false;
    }

    private User findUser(final String requestBody) {
        final Map<String, String> formData = parseFormData(requestBody);
        final String account = formData.get("account");
        final String password = formData.get("password");
        if (account == null || password == null) {
            return null;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return null;
        }
        return user.get();
    }

    private Map<String, String> parseFormData(final String requestBody) {
        final Map<String, String> formData = new HashMap<>();
        final String[] formFields = requestBody.split("&");

        for (String formField : formFields) {
            final String[] keyValue = formField.split("=", 2);
            if (keyValue.length < 2) {
                continue;
            }
            formData.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
        }
        return formData;
    }
}
