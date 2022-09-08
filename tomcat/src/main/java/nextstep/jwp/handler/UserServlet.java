package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.handler.RequestServlet;
import org.apache.coyote.http11.handler.ServletResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;

public class UserServlet implements RequestServlet {

    private static final String PASSWORD_KEY = "password";
    private static final String ACCOUNT_KEY = "account";
    private static final String EMAIL_KEY = "email";

    @Override
    public ServletResponseEntity doGet(final HttpRequest httpRequest, final HttpResponseHeader responseHeader) {
        return ServletResponseEntity.createWithResource("/register.html");
    }

    @Override
    public ServletResponseEntity doPost(final HttpRequest request, final HttpResponseHeader responseHeader) {
        validateQueryParams(request);

        final User user = new User(request.getParameter(ACCOUNT_KEY), request.getParameter(PASSWORD_KEY),
                request.getParameter(EMAIL_KEY));
        InMemoryUserRepository.save(user);

        responseHeader.addHeader("Location", "/index.html");
        return ServletResponseEntity.createResponseBody(HttpStatus.FOUND, responseHeader, EMPTY_BODY);
    }

    private void validateQueryParams(final HttpRequest request) {
        if (!request.containsParameter(ACCOUNT_KEY) || !request.containsParameter(PASSWORD_KEY)
                || !request.containsParameter(EMAIL_KEY)) {
            throw new IllegalArgumentException("No Parameters");
        }
    }
}
