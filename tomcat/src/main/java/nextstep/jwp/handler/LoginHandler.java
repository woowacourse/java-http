package nextstep.jwp.handler;

import static org.apache.coyote.http11.authorization.SessionManager.SESSION_MANAGER;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.authorization.Session;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class LoginHandler extends Handler {

    public static LoginHandler LOGIN_HANDLER = new LoginHandler();

    private static String JSESSIONID = "JSESSIONID";
    private static String SUCCESS_REDIRECT_URI = "/index.html";
    private static String FAIL_REDIRECT_URI = "/401.html";

    private LoginHandler() {
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        try {
            if (request.getCookieValue(JSESSIONID) != null) {
                throw new IllegalArgumentException("이미 로그인이 되어있습니다.");
            }

            final User loginUser = InMemoryUserRepository.findByAccount(request.getBodyValue("account"))
                    .orElseThrow(() -> new IllegalArgumentException("없는 회원입니다."));

            if (!loginUser.checkPassword(request.getBodyValue("password"))) {
                throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
            }

            final String jSessionId = String.valueOf(UUID.randomUUID());
            createSession(jSessionId, loginUser);

            log.info("user : " + loginUser);
            return createHandlerResult(HttpStatusCode.FOUND, SUCCESS_REDIRECT_URI, jSessionId);
        } catch (IllegalArgumentException e) {
            return createHandlerResult(HttpStatusCode.FOUND, FAIL_REDIRECT_URI);
        }
    }

    private void createSession(final String jSessionId, final User user) {
        final Session session = new Session(jSessionId);
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);
    }

    private HttpResponse createHandlerResult(final HttpStatusCode statusCode, final String redirectUri,
                                             final String jSessionId) {
        final Map<String, String> responseHeader = new LinkedHashMap<>();
        responseHeader.put("Location", redirectUri);
        responseHeader.put("Set-Cookie", JSESSIONID + "=" + jSessionId);
        return new HttpResponse(statusCode, responseHeader, "");
    }

    private HttpResponse createHandlerResult(final HttpStatusCode statusCode, final String redirectUri) {
        final Map<String, String> responseHeader = new LinkedHashMap<>();
        responseHeader.put("Location", redirectUri);
        return new HttpResponse(statusCode, responseHeader, "");
    }
}
