package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class HomeController extends AbstractController {

    private static final String ROOT_PAGE = "/";

    public HomeController(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public byte[] get(HttpRequest httpRequest) throws IOException {
        if (ROOT_PAGE.equals(httpRequest.uri())) {
            return HttpResponse.ok(httpRequest.resource(Controller.INDEX_PAGE));
        }
        return HttpResponse.ok(httpRequest.resource());
    }

    @Override
    public byte[] post(HttpRequest httpRequest) throws IOException {
        return HttpResponse.error(HttpError.FORBIDDEN);
    }

    @Override
    public byte[] error(HttpError httpError) throws IOException {
        return HttpResponse.error(HttpError.FORBIDDEN);
    }
}
