package org.apache.coyote.http11.component.handler;

import org.apache.coyote.http11.component.common.Version;
import org.apache.coyote.http11.component.common.body.TextTypeBody;
import org.apache.coyote.http11.component.request.HttpRequest;
import org.apache.coyote.http11.component.response.HttpResponse;
import org.apache.coyote.http11.component.response.ResponseHeader;
import org.apache.coyote.http11.component.response.ResponseLine;
import org.apache.coyote.http11.component.response.StatusCode;

import com.techcourse.db.InMemoryUserRepository;

public class LoginHandler implements HttpHandler {

    private static final String ID_QUERY_NAME = "account";
    private static final String PASSWORD_QUERY_NAME = "password";

    private final HttpRequest request;

    public LoginHandler(final HttpRequest request) {
        this.request = request;
    }

    @Override
    public String getUriPath() {
        return request.getPath();
    }

    @Override
    public HttpResponse handle() {
        final var identifier = request.getQueryParam(ID_QUERY_NAME);
        final var password = request.getQueryParam(PASSWORD_QUERY_NAME);
        validateUser(identifier, password);
        return createResponse();
    }

    private void validateUser(final String identifier, final String password) {
        final var user = InMemoryUserRepository.findByAccount(identifier)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 아이디"));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }
    }

    private HttpResponse createResponse() {
        final var found = new ResponseLine(new Version(1, 1), new StatusCode("FOUND", 302));
        final var responseHeader = new ResponseHeader();
        responseHeader.put("Location", "http://localhost:8080/index.html");
        return new HttpResponse(found, responseHeader, new TextTypeBody(""));
    }
}
