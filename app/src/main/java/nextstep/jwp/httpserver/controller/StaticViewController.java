package nextstep.jwp.httpserver.controller;

import java.util.Map;

import nextstep.jwp.httpserver.domain.StatusCode;
import nextstep.jwp.httpserver.domain.response.HttpResponse;

public class StaticViewController implements Handler {

    @Override
    public HttpResponse handle(Map<String, String> param) {
        return new HttpResponse.Builder()
                .statusCode(StatusCode.OK)
                .build();
    }
}
