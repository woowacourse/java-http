package nextstep.jwp.server.handler.controller.standard;

import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.response.HttpResponse;

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
