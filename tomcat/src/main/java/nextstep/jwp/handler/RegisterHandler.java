package nextstep.jwp.handler;

import static nextstep.jwp.service.UserService.USER_SERVICE;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.dto.RegisterRequest;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class RegisterHandler extends Handler {

    public static final RegisterHandler REGISTER_HANDLER = new RegisterHandler();

    private static String JSESSIONID = "JSESSIONID";
    private static String SUCCESS_REDIRECT_URI = "/index.html";
    private static String FAIL_REDIRECT_URI = "/401.html";

    private RegisterHandler() {
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        try {
            if (request.getCookieValue(JSESSIONID) != null) {
                throw new IllegalArgumentException("이미 로그인이 되어있습니다.");
            }

            final RegisterRequest registerRequest = new RegisterRequest(request.getBodyValue("account"),
                    request.getBodyValue("password"), request.getBodyValue("email"));

            final String jSessionId = USER_SERVICE.signUp(registerRequest);
            return createHandlerResult(HttpStatusCode.FOUND, SUCCESS_REDIRECT_URI, jSessionId);
        } catch (IllegalArgumentException e) {
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
