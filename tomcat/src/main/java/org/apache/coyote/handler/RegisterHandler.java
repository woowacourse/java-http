package org.apache.coyote.handler;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Handler;
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
    private final String rootContextPath;

    public RegisterHandler(final String path, final String rootContextPath) {
        this.path = path;
        this.rootContextPath = rootContextPath;
    }

    @Override
    public boolean supports(final Request request) {
        return isPostMethod(request) && isRegisterRequest(request);
    }

    private boolean isPostMethod(final Request request) {
        return request.matchesByMethod(HttpMethod.POST);
    }

    private boolean isRegisterRequest(final Request request) {
        return request.matchesByPath(path, rootContextPath) && request.hasQueryParameters();
    }

    @Override
    public Response service(final Request request) throws IOException {
        final String account = request.findQueryParameterValue(ACCOUNT_KEY);
        final String password = request.findQueryParameterValue(PASSWORD_KEY);
        final String email = request.findQueryParameterValue(EMAIL_KEY);

        if (isInvalidQueryParameter(account) || isInvalidQueryParameter(password) || isInvalidQueryParameter(email)) {
            return Response.of(request, HttpStatusCode.BAD_REQUEST, ContentType.JSON, HttpConsts.BLANK);
        }

        final User user = new User(account, password, email);

        InMemoryUserRepository.save(user);

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
