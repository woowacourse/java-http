package nextstep.jwp.handler.get;

import static org.apache.coyote.http11.authorization.SessionManager.SESSION_MANAGER;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class LoginPageHandler extends HandlerForResource {

    public static final LoginPageHandler LOGIN_PAGE_HANDLER = new LoginPageHandler();
    private static String JSESSIONID = "JSESSIONID";

    private LoginPageHandler() {
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        String sessionId;
        if ((sessionId = request.getCookieValue(JSESSIONID)) != null &&
                SESSION_MANAGER.findSession(sessionId).isPresent()) {
            return new HttpResponse(HttpStatusCode.FOUND, createRedirectResponse("/index.html"), "");
        }

        final String uri = request.getUri();
        return new HttpResponse(HttpStatusCode.OK, createResponseHeader(uri),
                getResponseBody(uri));
    }

    private Map<String, String> createRedirectResponse(final String redirectUri) {
        final Map<String, String> header = new HashMap<>();
        header.put("Location", redirectUri);
        return header;
    }
}
