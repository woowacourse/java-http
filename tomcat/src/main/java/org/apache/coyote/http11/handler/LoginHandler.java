package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.http.HttpVersion.HTTP11;
import static org.apache.coyote.http11.header.ContentType.UTF_8;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.http.response.HttpStatus.OK;

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
            final Map<String, String> queryParams = Parser.parseQueryParams(path);
            final User user = getUserByQueryParams(queryParams);

            final String body = user.toString();
            final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE, UTF_8.getValue());
            final HttpHeader contentLength = HttpHeader.of(CONTENT_LENGTH, String.valueOf(body.length()));

            return HttpResponse.of(HTTP11, OK, body, contentType, contentLength);
        }
        return generateResourceResponse(httpRequest);
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
