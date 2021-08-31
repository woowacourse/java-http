package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpError;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class ExceptionController extends AbstractController {

    public ExceptionController(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    byte[] get(HttpRequest httpRequest) throws IOException {
        return HttpResponse.error(HttpError.NOTFOUND);
    }

    @Override
    byte[] post(HttpRequest httpRequest) throws IOException {
        return HttpResponse.error(HttpError.NOTFOUND);
    }

    @Override
    byte[] error(HttpError httpError) throws IOException {
        return HttpResponse.error(HttpError.NOTFOUND);
    }
}
