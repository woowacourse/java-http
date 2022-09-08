package nextstep.jwp.controller.auth;

import static org.apache.coyote.http11.header.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.http.HttpVersion.HTTP11;
import static org.apache.coyote.http11.http.response.HttpStatus.REDIRECT;

import java.util.Map;
import nextstep.jwp.application.UserService;
import nextstep.jwp.controller.Handler;
import nextstep.jwp.controller.resource.ResourceUrls;
import nextstep.jwp.dto.UserLoginRequest;
import org.apache.catalina.webutils.Parser;
import org.apache.coyote.http11.header.HttpCookie;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class LoginHandler implements Handler {

    private final UserService userService = UserService.getInstance();

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String body = httpRequest.getBody();
        return generateLoginResponse(body);
    }

    protected HttpResponse generateLoginResponse(final String body) {
        final Map<String, String> queryParams = Parser.parseQueryParams(body);
        try {
            final UserLoginRequest userLoginRequest = getUserLoginRequest(queryParams);
            userService.login(userLoginRequest);
            final HttpHeader location = HttpHeader.of(LOCATION.getValue(), ResourceUrls.INDEX_HTML);
            final HttpCookie cookie = SessionManager.createCookie();
            final HttpHeader cookieHeader = HttpHeader.of("Set-Cookie", cookie.toHeaderValue());
            return HttpResponse.of(HTTP11, REDIRECT, location, cookieHeader);
        } catch (IllegalArgumentException exception) {
            final HttpHeader location = HttpHeader.of(LOCATION.getValue(), ResourceUrls.UNAUTHORIZED_HTML);
            return HttpResponse.of(HTTP11, REDIRECT, location);
        }
    }

    private UserLoginRequest getUserLoginRequest(final Map<String, String> queryParams) {
        validateLoginParams(queryParams);
        return new UserLoginRequest(queryParams.get("account"),
                queryParams.get("password"));
    }

    private void validateLoginParams(final Map<String, String> queryParams) {
        if (!queryParams.containsKey("account") || !queryParams.containsKey("password")) {
            throw new IllegalArgumentException("account와 password 정보가 입력되지 않았습니다.");
        }
    }
}
