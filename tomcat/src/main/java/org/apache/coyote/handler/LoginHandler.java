package org.apache.coyote.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Handler;
import org.apache.coyote.handler.exception.LoginFailureException;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpSession;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    public static final String ACCOUNT_KEY = "account";
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
        return isPostMethod(request) && isLoginRequest(request) && request.hasQueryParameters();
    }

    private boolean isPostMethod(final Request request) {
        return request.matchesByMethod(HttpMethod.POST);
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

            final HttpSession session = request.getSession(true);
            session.setAttribute(ACCOUNT_KEY, user);
            final HttpCookie cookie = HttpCookie.fromSessionId(session.getId());

            return Response.of(
                    request,
                    HttpStatusCode.FOUND,
                    ContentType.JSON,
                    user.toString(),
                    cookie,
                    new HeaderDto("Location", "/index.html")
            );
        } catch (LoginFailureException ex) {
            log.info("login failed : ", ex);

            return Response.of(
                    request,
                    HttpStatusCode.FOUND,
                    ContentType.JSON,
                    HttpConsts.BLANK,
                    new HeaderDto("Location", "/401.html")
            );
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
