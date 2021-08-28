package nextstep.jwp.infrastructure.http.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.infrastructure.http.HttpHeaders;
import nextstep.jwp.infrastructure.http.View;
import nextstep.jwp.infrastructure.http.objectmapper.DataMapper;
import nextstep.jwp.infrastructure.http.objectmapper.UrlEncodingMapper;
import nextstep.jwp.infrastructure.http.request.HttpMethod;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.HttpRequestLine;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.HttpStatusCode;
import nextstep.jwp.infrastructure.http.response.HttpStatusLine;
import nextstep.jwp.model.User;

public class PostRegisterController implements Controller{

    private static final DataMapper DATA_MAPPER = new UrlEncodingMapper();
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    public HttpRequestLine requestLine() {
        return new HttpRequestLine(HttpMethod.POST, "/register");
    }

    @Override
    public View handle(final HttpRequest request) {
        final Map<String, String> body = DATA_MAPPER.parse(request.getMessageBody());
        validateBody(request, body);
        final User user = new User(body.get(ACCOUNT), body.get(PASSWORD), body.get(EMAIL));
        InMemoryUserRepository.save(user);

        return View.buildByHttpResponse(
            new HttpResponse(
                new HttpStatusLine(HttpStatusCode.FOUND),
                new HttpHeaders.Builder()
                    .header("Location", "/index.html")
                    .build()
            )
        );
    }

    private void validateBody(final HttpRequest request, final Map<String, String> body) {
        if (!body.containsKey(ACCOUNT) || !body.containsKey(PASSWORD) || !body.containsKey(EMAIL)) {
            throw new IllegalArgumentException(String.format("Invalid body format. (%s)", request.getMessageBody()));
        }
    }
}
