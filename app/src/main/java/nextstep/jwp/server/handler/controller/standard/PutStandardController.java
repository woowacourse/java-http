package nextstep.jwp.server.handler.controller.standard;

import nextstep.jwp.http.header.request.HttpRequest;
import nextstep.jwp.http.header.response.HttpResponse;

public class PutStandardController implements StandardController {

    @Override
    public HttpResponse doService(HttpRequest httpRequest) {
        return null;
    }

    @Override
    public boolean isSatisfiedBy(HttpRequest httpRequest) {
        return false;
    }
}
