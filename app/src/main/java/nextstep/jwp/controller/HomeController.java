package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.RequestHandler;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public HomeController(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public byte[] get(HttpRequest httpRequest) throws IOException {
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
