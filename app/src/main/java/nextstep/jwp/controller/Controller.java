package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class Controller extends AbstractController {

    public Controller(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    byte[] get(HttpRequest httpRequest) throws IOException {
        return HttpResponse.ok(httpRequest.resource());
    }

    @Override
    byte[] post(HttpRequest httpRequest) {
        return new byte[0];
    }

    @Override
    byte[] error(HttpError httpError) {
        return new byte[0];
    }
}
