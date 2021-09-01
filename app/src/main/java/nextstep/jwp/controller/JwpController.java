package nextstep.jwp.controller;

import nextstep.jwp.http.HttpContentType;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

import java.util.Map;
import java.util.function.Function;

public class JwpController extends AbstractController {
    @Override
    void doGet(final HttpRequest request, HttpResponse response) {
        final Map<String, Function<HttpRequest, HttpResponse>> mappedFunction = Map.of(
                "index", this::getBasicPage,
                "401", this::getBasicPage,
                "404", this::getBasicPage,
                "500", this::getBasicPage
        );
        HttpResponse httpResponse = getHttpResponse(request, mappedFunction);
        response.setResponse(httpResponse.getResponse());
    }

    @Override
    void doPost(final HttpRequest request, HttpResponse response) {
    }

    private HttpResponse getBasicPage(final HttpRequest request) {
        return new HttpResponse(HttpStatus.OK, HttpContentType.NOTHING, request.getUrl());
    }
}
