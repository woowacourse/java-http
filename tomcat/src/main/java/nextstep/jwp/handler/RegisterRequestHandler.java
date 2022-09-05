package nextstep.jwp.handler;

import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpVersion;
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
        return null;
    }

    @Override
    public HttpResponse handleHttpGetRequest(final HttpRequest httpRequest) {
        String responseBody = ResourcesUtil.readResource(httpRequest.getFilePath(), this.getClass());
        return HttpResponse.ok(httpVersion, responseBody);
    }

    @Override
    public HttpResponse handleHttpPostRequest(final HttpRequest httpRequest) {

        return null;
    }
}
