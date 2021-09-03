package nextstep.jwp.controller;

import nextstep.jwp.http.HttpContentType;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

import java.util.Map;

public class JwpController extends AbstractController {
    public static final String INDEX_PAGE = "index.html";
    public static final HttpResponse NOT_FOUND_RESPONSE = new HttpResponse(HttpStatus.NOT_FOUND, HttpContentType.NOTHING, "404.html");
    protected static final HttpResponse UNAUTHORIZED_RESPONSE = new HttpResponse(HttpStatus.UNAUTHORIZED, HttpContentType.NOTHING, "401.html");
    protected static final HttpResponse INTERNAL_SERVER_RESPONSE = new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpContentType.NOTHING, "500.html");

    @Override
    protected void doGet(final HttpRequest request, HttpResponse response) {
        final Map<String, HttpResponse> mappedResponse = Map.of(
                "index.html", getIndex(),
                "index", getIndex(),
                "401", UNAUTHORIZED_RESPONSE,
                "404", NOT_FOUND_RESPONSE,
                "500", INTERNAL_SERVER_RESPONSE
        );
        HttpResponse httpResponse = mappedResponse.entrySet().stream()
                .filter(entry -> request.containsFunctionInUrl(entry.getKey()))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(NOT_FOUND_RESPONSE);
        response.setResponse(httpResponse.getResponse());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    private HttpResponse getIndex() {
        return new HttpResponse(HttpStatus.OK, HttpContentType.NOTHING, INDEX_PAGE);
    }
}
