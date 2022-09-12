package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.handler.HandlerResponseEntity;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponseHeader;

public class UserController extends HttpRequestHandler {

    private static final String PASSWORD_KEY = "password";
    private static final String ACCOUNT_KEY = "account";
    private static final String EMAIL_KEY = "email";

    @Override
    public HandlerResponseEntity doGet(final HttpRequest httpRequest, final HttpResponseHeader responseHeader) {
        return HandlerResponseEntity.createWithResource("/register.html");
    }

    @Override
    public HandlerResponseEntity doPost(final HttpRequest request, final HttpResponseHeader responseHeader) {
        validateQueryParams(request);

        final User user = new User(request.getParameter(ACCOUNT_KEY), request.getParameter(PASSWORD_KEY),
                request.getParameter(EMAIL_KEY));
        InMemoryUserRepository.save(user);

        responseHeader.addHeader("Location", "/index.html");
        return HandlerResponseEntity.createResponseBody(HttpStatus.FOUND, responseHeader, EMPTY_BODY);
    }

    private void validateQueryParams(final HttpRequest request) {
        if (!request.containsParameter(ACCOUNT_KEY) || !request.containsParameter(PASSWORD_KEY)
                || !request.containsParameter(EMAIL_KEY)) {
            throw new IllegalArgumentException("No Parameters");
        }
    }
}
