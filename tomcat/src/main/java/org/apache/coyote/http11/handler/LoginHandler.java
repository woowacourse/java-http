package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.header.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.http.HttpVersion.HTTP11;
import static org.apache.coyote.http11.http.response.HttpStatus.REDIRECT;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.utils.Parser;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class LoginHandler extends ResourceHandler{

    private static final String QUERY_PARAMETER_START_LETTER = "?";

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String path = httpRequest.getStartLine().getPath();
        if (path.contains(QUERY_PARAMETER_START_LETTER)) {
            return generateLoginResponse(path);
        }
        return generateResourceResponse("/login.html");
    }

    private HttpResponse generateLoginResponse(final String path) {
        final Map<String, String> queryParams = Parser.parseQueryParams(path);
        try {
            final User user = getUserByQueryParams(queryParams);
            final HttpHeader location = HttpHeader.of(LOCATION, "/index.html");

            return HttpResponse.of(HTTP11, REDIRECT, location);
        } catch (IllegalArgumentException exception) {
            final HttpHeader location = HttpHeader.of(LOCATION, "/401.html");
            return HttpResponse.of(HTTP11, REDIRECT, location);
        }
    }

    private User getUserByQueryParams(final Map<String, String> queryParams) {
        validateLoginParams(queryParams);
        final String account = queryParams.get("account");
        final String password = queryParams.get("password");
        final User user = InMemoryUserRepository.getByAccount(account);
        validateUserPassword(password, user);
        return user;
    }

    private void validateUserPassword(final String password, final User user) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 정확하지 않습니다. : " + password);
        }
    }

    private void validateLoginParams(final Map<String, String> queryParams) {
        if (!queryParams.containsKey("account") || !queryParams.containsKey("password")) {
            throw new IllegalArgumentException("account와 password 정보가 입력되지 않았습니다.");
        }
    }
}
