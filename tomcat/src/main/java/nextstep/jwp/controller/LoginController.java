package nextstep.jwp.controller;

import static nextstep.jwp.controller.ResourceUrls.INDEX_HTML;
import static nextstep.jwp.controller.ResourceUrls.LOGIN_HTML;
import static nextstep.jwp.controller.ResourceUrls.UNAUTHORIZED_HTML;

import java.util.Map;
import java.util.NoSuchElementException;
import nextstep.jwp.application.AuthorizeService;
import nextstep.jwp.application.UserService;
import nextstep.jwp.dto.UserLoginRequest;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.webutils.Parser;
import org.apache.coyote.http11.header.HttpCookie;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class LoginController extends ResourceController {

    private final AuthorizeService authorizeService = AuthorizeService.getInstance();
    private final UserService userService = UserService.getInstance();

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (authorizeService.isAuthorized(httpRequest)) {
            setRedirectHeader(httpResponse, INDEX_HTML);
            return;
        }
        setResource(LOGIN_HTML.getValue(), httpResponse);
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final String body = httpRequest.getBody();
        login(body, httpResponse);
    }

    private void login(final String body, final HttpResponse httpResponse) {
        final Map<String, String> queryParams = Parser.parseQueryParams(body);
        try {
            final UserLoginRequest userLoginRequest = getUserLoginRequest(queryParams);
            userService.login(userLoginRequest);
            final HttpCookie cookie = SessionManager.createCookie();
            final HttpHeader cookieHeader = HttpHeader.of("Set-Cookie", cookie.toHeaderValue());
            setRedirectHeader(httpResponse, INDEX_HTML);
            httpResponse.addHeader(cookieHeader);
        } catch (IllegalArgumentException exception) {
            setRedirectHeader(httpResponse, UNAUTHORIZED_HTML);
        } catch (NoSuchElementException exception) {
            setRedirectHeader(httpResponse, LOGIN_HTML);
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
