package org.apache.coyote.http11.component.handler;

import org.apache.coyote.http11.component.exception.AuthenticationException;
import org.apache.coyote.http11.component.request.HttpRequest;
import org.apache.coyote.http11.component.resource.StaticResourceFinder;
import org.apache.coyote.http11.component.response.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;

public class LoginHandler implements HttpHandler {

    private static final String LOGIN_SUCCESSFUL_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String LOGIN_HTML = "login.html";
    private static final String ID_QUERY_NAME = "account";
    private static final String PASSWORD_QUERY_NAME = "password";

    private final String path;

    public LoginHandler(final String path) {
        this.path = path;
    }

    @Override
    public String getUriPath() {
        return path;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        if (request.hasNotQuery()) {
            return StaticResourceFinder.render(LOGIN_HTML);
        }
        final var identifier = request.getQueryParam(ID_QUERY_NAME);
        final var password = request.getQueryParam(PASSWORD_QUERY_NAME);
        validateUser(identifier, password);
        return StaticResourceFinder.renderRedirect(LOGIN_SUCCESSFUL_REDIRECT_URI);
    }

    private void validateUser(final String identifier, final String password) {
        final var user = InMemoryUserRepository.findByAccount(identifier)
                .orElseThrow(AuthenticationException::new);
        if (!user.checkPassword(password)) {
            throw new AuthenticationException();
        }
    }
}
