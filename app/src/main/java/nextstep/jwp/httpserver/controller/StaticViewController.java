package nextstep.jwp.httpserver.controller;

import nextstep.jwp.httpserver.domain.StatusCode;
import nextstep.jwp.httpserver.domain.response.HttpResponse;

public class StaticViewController implements Handler {

    @Override
    public HttpResponse handle() {
        return new HttpResponse.Builder()
                .statusCode(StatusCode.OK)
                .build();
    }
}
