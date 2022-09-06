package nextstep.jwp.handler;

import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpRequestBody;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.Location;
import nextstep.jwp.model.User;
import nextstep.jwp.util.ResourcesUtil;

public class RegisterRequestHandler implements HttpRequestHandler {

    private static final String REGISTER_PATH = "/register";

    private final HttpVersion httpVersion;

    public RegisterRequestHandler(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    public HttpResponse handleHttpRequest(final HttpRequest httpRequest) {
        if (httpRequest.getHttpMethod().equals(HttpMethod.GET)) {
            return handleHttpGetRequest(httpRequest);
        }
        if (httpRequest.getHttpMethod().equals(HttpMethod.POST)) {
            return handleHttpPostRequest(httpRequest);
        }
        return null;
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
