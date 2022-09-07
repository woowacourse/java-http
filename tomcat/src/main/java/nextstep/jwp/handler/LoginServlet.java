package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.handler.RequestServlet;
import org.apache.coyote.http11.handler.ServletResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet implements RequestServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    @Override
    public ServletResponseEntity doGet(final HttpRequest httpRequest, final HttpResponseHeader responseHeader) {
        return ServletResponseEntity.createWithResource("/login.html");
    }

    @Override
    public ServletResponseEntity doPost(final HttpRequest request, final HttpResponseHeader responseHeader) {
        validateQueryParams(request);

        InMemoryUserRepository.findByAccount(request.getParameter(ACCOUNT_KEY))
                .ifPresentOrElse(it -> validateUserLogin(request.getParameter(PASSWORD_KEY), it), () -> {
                    throw new IllegalArgumentException("User not found");
                });

        responseHeader.addHeader("Location", "/index.html");
        return ServletResponseEntity.createResponseBody(HttpStatus.FOUND, responseHeader, EMPTY_BODY);
    }

    private void validateQueryParams(final HttpRequest request) {
        if (!request.containsParameter(ACCOUNT_KEY) || !request.containsParameter(PASSWORD_KEY)) {
            throw new IllegalArgumentException("No Parameters");
        }
    }

    private void validateUserLogin(final String password, final User user) {
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("User not found");
        }

        log.info(user.toString());
    }
}
