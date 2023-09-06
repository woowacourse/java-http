package nextstep.handler;

import java.io.IOException;
import nextstep.jwp.application.UserService;
import org.apache.coyote.Handler;
import nextstep.handler.exception.InvalidQueryParameterException;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.HttpHeaderConsts;
import org.apache.coyote.http.util.HttpMethod;

public class RegisterHandler implements Handler {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";

    private final String path;
    private final UserService userService;

    public RegisterHandler(final String path, final UserService userService) {
        this.path = path;
        this.userService = userService;
    }

    @Override
    public boolean supports(final Request request, final String contextPath) {
        return isPostMethod(request) && isRegisterRequest(request, contextPath);
    }

    private boolean isPostMethod(final Request request) {
        return request.matchesByMethod(HttpMethod.POST);
    }

    private boolean isRegisterRequest(final Request request, final String contextPath) {
        return request.matchesByPathExcludingContextPath(path, contextPath) && request.hasQueryParameters();
    }

    @Override
    public Response service(final Request request, final String ignoreResourcePath) throws IOException {
        final String account = request.findParameterValue(ACCOUNT_KEY);
        final String password = request.findParameterValue(PASSWORD_KEY);
        final String email = request.findParameterValue(EMAIL_KEY);

        if (isInvalidQueryParameter(account) || isInvalidQueryParameter(password) || isInvalidQueryParameter(email)) {
            throw new InvalidQueryParameterException();
        }

        userService.register(account, password, email);

        return Response.of(
                request,
                HttpStatusCode.FOUND,
                ContentType.JSON,
                HttpConsts.BLANK,
                new HeaderDto(HttpHeaderConsts.LOCATION, "/login")
        );
    }

    private boolean isInvalidQueryParameter(final String targetParameter) {
        return targetParameter == null || targetParameter.isEmpty() || targetParameter.isBlank();
    }
}
