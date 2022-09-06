package nextstep.jwp.handler;

import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpRequestBody;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.Location;
import nextstep.jwp.model.User;
import nextstep.jwp.util.ResourcesUtil;

public final class RegisterRequestHandler extends AbstractHttpRequestHandler {

    private final HttpVersion httpVersion;

    public RegisterRequestHandler(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    public HttpResponse handleHttpGetRequest(final HttpRequest httpRequest) {
        String responseBody = ResourcesUtil.readResource(httpRequest.getFilePath(), this.getClass());
        return HttpResponse.ok(httpVersion, HttpCookie.empty(), responseBody);
    }

    @Override
    public HttpResponse handleHttpPostRequest(final HttpRequest httpRequest) {
        HttpRequestBody httpRequestBody = httpRequest.getHttpRequestBody();
        registerUser(httpRequestBody);

        HttpCookie responseCookie = HttpCookie.empty();
        if (httpRequest.isEmptySessionId()) {
            responseCookie.addSessionId(String.valueOf(UUID.randomUUID()));
        }
        return HttpResponse.found(httpVersion, responseCookie, new Location("/index.html"));
    }

    private void registerUser(final HttpRequestBody httpRequestBody) {
        String account = httpRequestBody.getValue("account");
        String email = httpRequestBody.getValue("email");
        String password = httpRequestBody.getValue("password");
        InMemoryUserRepository.save(new User(account, password, email));
    }
}
