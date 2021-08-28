package nextstep.jwp.infrastructure.http.controller;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.infrastructure.http.HttpHeaders;
import nextstep.jwp.infrastructure.http.View;
import nextstep.jwp.infrastructure.http.request.HttpMethod;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.HttpRequestLine;
import nextstep.jwp.infrastructure.http.request.URI;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.HttpStatusCode;
import nextstep.jwp.infrastructure.http.response.HttpStatusLine;

public class LoginController implements Controller {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final List<String> REQUIRED_PARAMETERS = Arrays.asList(ACCOUNT, PASSWORD);

    @Override
    public HttpRequestLine requestLine() {
        return new HttpRequestLine(HttpMethod.GET, "/login");
    }

    @Override
    public View handle(final HttpRequest request) {
        final URI uri = request.getRequestLine().getUri();

        if (uri.hasKeys(REQUIRED_PARAMETERS)) {
            final String location = locationByLogin(uri);

            return View.buildByHttpResponse(
                new HttpResponse(
                    new HttpStatusLine(HttpStatusCode.FOUND),
                    new HttpHeaders.Builder()
                        .header("Location", location)
                        .build()
                )
            );
        }

        return View.buildByResource("/login.html");
    }

    public String locationByLogin(final URI uri) {
        final String account = uri.getValue(ACCOUNT);
        final String password = uri.getValue(PASSWORD);

        if (InMemoryUserRepository.existsByAccountAndPassword(account, password)) {
            return "/index.html";
        }
        return "/401.html";
    }
}
