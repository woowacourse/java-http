package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class Controller extends AbstractController {

    public static final String INDEX_PAGE = "/index.html";

    public Controller(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    byte[] get(HttpRequest httpRequest) throws IOException {
        return HttpResponse.ok(httpRequest.resource(INDEX_PAGE));
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
