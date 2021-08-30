package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.infrastructure.http.HttpHeaders;
import nextstep.jwp.infrastructure.http.objectmapper.DataMapper;
import nextstep.jwp.infrastructure.http.objectmapper.UrlEncodingMapper;
import nextstep.jwp.infrastructure.http.request.HttpMethod;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.HttpRequestLine;
import nextstep.jwp.infrastructure.http.request.URI;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.HttpStatusCode;
import nextstep.jwp.infrastructure.http.response.HttpStatusLine;
import nextstep.jwp.infrastructure.http.view.HttpResponseView;
import nextstep.jwp.infrastructure.http.view.View;

public class PostLoginController implements Controller {

    private static final DataMapper DATA_MAPPER = new UrlEncodingMapper();
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final List<String> REQUIRED_PARAMETERS = Arrays.asList(ACCOUNT, PASSWORD);

    @Override
    public HttpRequestLine requestLine() {
        return new HttpRequestLine(HttpMethod.POST, "/login");
    }

    @Override
    public View handle(final HttpRequest request) {
        final Map<String, String> body = DATA_MAPPER.parse(request.getMessageBody());
        if (!containsAllKey(body)) {
            return redirectView("/500.html");
        }

        return redirectView(locationByLogin(body));
    }

    private boolean containsAllKey(final Map<String, String> body) {
        return REQUIRED_PARAMETERS.stream()
            .allMatch(body::containsKey);
    }

    private HttpResponseView redirectView(final String location) {
        return new HttpResponseView(
            new HttpResponse(
                new HttpStatusLine(HttpStatusCode.FOUND),
                new HttpHeaders.Builder()
                    .header("Location", location)
                    .build()
            )
        );
    }

    public String locationByLogin(final Map<String, String> body) {
        final String account = body.get(ACCOUNT);
        final String password = body.get(PASSWORD);

        if (InMemoryUserRepository.existsByAccountAndPassword(account, password)) {
            return "/index.html";
        }
        return "/401.html";
    }
}
