package nextstep.jwp.handler;

import static nextstep.jwp.service.UserService.USER_SERVICE;

import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.dto.LoginRequest;
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

            final String jSessionId = USER_SERVICE.login(
                    new LoginRequest(request.getBodyValue("account"), request.getBodyValue("password")));
            return createHandlerResult(HttpStatusCode.FOUND, SUCCESS_REDIRECT_URI, jSessionId);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return createHandlerResult(HttpStatusCode.FOUND, FAIL_REDIRECT_URI);
        }
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
