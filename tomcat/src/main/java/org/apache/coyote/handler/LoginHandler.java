package org.apache.coyote.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Handler;
import org.apache.coyote.handler.exception.LoginFailureException;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private final String path;
    private final String rootContextPath;

    public LoginHandler(final String path, final String rootContextPath) {
        this.path = path;
        this.rootContextPath = rootContextPath;
    }

    @Override
    public boolean supports(final Request request) {
        return isGetMethod(request) && isLoginRequest(request) && request.hasQueryParameters();
    }

    private boolean isGetMethod(final Request request) {
        return request.matchesByMethod(HttpMethod.GET);
    }

    private boolean isLoginRequest(final Request request) {
        return request.matchesByPath(path, rootContextPath);
    }

    @Override
    public Response service(final Request request) throws IOException {
        final String account = request.findQueryParameterValue(ACCOUNT_KEY);
        final String password = request.findQueryParameterValue(PASSWORD_KEY);

        if (validateAccountInfo(account, password)) {
            return Response.of(request, HttpStatusCode.BAD_REQUEST, ContentType.JSON, HttpConsts.BLANK);
        }

        try {
            final User user = InMemoryUserRepository.findByAccount(account)
                                                    .orElseThrow(LoginFailureException::new);

            validatePassword(password, user);

            log.info("login success : {}", user);

            return Response.of(request, HttpStatusCode.OK, ContentType.JSON, user.toString());
        } catch (LoginFailureException ex) {
            log.info("login failed : ", ex);

            return Response.of(request, HttpStatusCode.BAD_REQUEST, ContentType.JSON, ex.getMessage());
        }
    }

    private boolean validateAccountInfo(final String account, final String password) {
        return account == null || password == null;
    }

    private void validatePassword(final String password, final User user) {
        if (!user.checkPassword(password)) {
            throw new LoginFailureException();
        }
    }
}
