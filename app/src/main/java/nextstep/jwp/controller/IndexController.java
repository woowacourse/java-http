package nextstep.jwp.controller;

import nextstep.jwp.domain.request.HttpRequest;
import nextstep.jwp.domain.response.HttpResponse;
import nextstep.jwp.domain.response.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IndexController implements Controller {

    private final Map<String, HttpStatus> invalidResponseMap = new HashMap<>();

    public IndexController() {
        invalidResponseMap.put("/400.html", HttpStatus.BAD_REQUEST);
        invalidResponseMap.put("/401.html", HttpStatus.UNAUTHORIZED);
        invalidResponseMap.put("/404.html", HttpStatus.NOT_FOUND);
    }

    @Override
    public HttpResponse service(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        try {
            String uri = request.getUri();
            return findHttpResponse(request, response, uri);
        } catch (Exception e) {
            return null;
        }
    }

    private HttpResponse findHttpResponse(HttpRequest request, HttpResponse response, String uri) throws IOException {
        if (isInvalidUri(uri)) {
            return response.respond(uri, invalidResponseMap.get(uri));
        }
        return response.respond(request.getUri());
    }

    private boolean isInvalidUri(String uri) {
        return Objects.nonNull(invalidResponseMap.get(uri));
    }
}
