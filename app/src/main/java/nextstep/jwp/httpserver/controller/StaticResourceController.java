package nextstep.jwp.httpserver.controller;

import java.util.Map;

import nextstep.jwp.httpserver.domain.StatusCode;
import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;

public class StaticResourceController implements Controller {

    @Override
    public HttpResponse service(HttpRequest httpRequest, Map<String, String> param) {
        return new HttpResponse.Builder()
                .statusCode(StatusCode.OK)
                .build();
    }
}
