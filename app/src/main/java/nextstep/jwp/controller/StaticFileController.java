package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;

public class StaticFileController extends AbstractController {

    private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
    private static final List<String> URI_PATHS = Arrays.asList("/assets", "/js", "/css");

    @Override
    boolean isMatchingHttpMethod(HttpRequest httpRequest) {
        return HTTP_METHOD == httpRequest.getHttpMethod();
    }

    @Override
    boolean isMatchingUriPath(HttpRequest httpRequest) {
        return URI_PATHS.stream()
            .anyMatch(it -> httpRequest.getPath().startsWith(it));
    }

    @Override
    public HttpResponse run(HttpRequest httpRequest) {
        if (httpRequest.getPath().endsWith(".css")) {
            return super.applyCSSFile(httpRequest.getPath());
        }
        return super.renderPage(httpRequest.getPath());
    }
}
