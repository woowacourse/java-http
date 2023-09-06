package nextstep.handler;

import java.io.IOException;
import nextstep.jwp.application.UserService;
import nextstep.jwp.model.User;
import org.apache.coyote.Handler;
import nextstep.handler.exception.InvalidQueryParameterException;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpSession;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.HttpHeaderConsts;
import org.apache.coyote.http.util.HttpMethod;

public class LoginHandler implements Handler {

    public static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    private final String path;
    private final UserService userService;

    public LoginHandler(final String path, final UserService userService) {
        this.path = path;
        this.userService = userService;
    }

    @Override
    public boolean supports(final Request request, final String contextPath) {
        return isPostMethod(request) && isLoginRequest(request, contextPath) && request.hasQueryParameters();
    }

    private boolean isPostMethod(final Request request) {
        return request.matchesByMethod(HttpMethod.POST);
    }

    private boolean isLoginRequest(final Request request, final String contextPath) {
        return request.matchesByPathExcludingRootContextPath(path, contextPath);
    }

    @Override
    public Response service(final Request request, final String ignoreResourcePath) throws IOException {
        final String account = request.findParameterValue(ACCOUNT_KEY);
        final String password = request.findParameterValue(PASSWORD_KEY);

        if (isInvalidQueryParameter(account) || isInvalidQueryParameter(password)) {
            throw new InvalidQueryParameterException();
        }

        final User loginUser = userService.login(account, password);

        final HttpSession session = request.getSession(true);
        session.setAttribute(ACCOUNT_KEY, loginUser);
        final HttpCookie cookie = HttpCookie.fromSessionId(session.getId());

        return Response.of(
                request,
                HttpStatusCode.FOUND,
                ContentType.JSON,
                loginUser.toString(),
                cookie,
                new HeaderDto(HttpHeaderConsts.LOCATION, "/index.html")
        );
    }

    private boolean isInvalidQueryParameter(final String targetParameter) {
        return targetParameter == null || targetParameter.isEmpty() || targetParameter.isBlank();
    }
}
