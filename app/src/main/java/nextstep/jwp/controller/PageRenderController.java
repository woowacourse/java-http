package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;

public class PageRenderController extends AbstractController {

    private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
    private static final List<String> URI_PATHS = Arrays.asList("/index.html", "/register", "/login",
        "/401.html", "/404.html", "/500.html");

    @Override
    boolean isMatchingHttpMethod(HttpRequest httpRequest) {
        return HTTP_METHOD == httpRequest.getHttpMethod();
    }

    @Override
    boolean isMatchingUriPath(HttpRequest httpRequest) {
        return URI_PATHS.stream()
            .anyMatch(it -> it.equals(httpRequest.getPath()));
    }

    @Override
    public HttpResponse run(HttpRequest httpRequest) {
        return super.renderPage(httpRequest.getPath());
    }
}
