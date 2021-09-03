package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class StaticFileController extends AbstractController {

    private static final List<String> URI_PATHS = Arrays.asList("/assets", "/js", "/css");

    @Override
    boolean isMatchingUriPath(HttpRequest httpRequest) {
        return URI_PATHS.stream()
            .anyMatch(it -> httpRequest.getPath().startsWith(it));
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        return super.renderPage(httpRequest, ContentType.fromPath(httpRequest.getPath()));
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        return super.redirect(getNotFoundErrorRedirectUrl());
    }
}
