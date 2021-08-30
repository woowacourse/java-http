package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class ExceptionController extends AbstractController {

    public ExceptionController(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public byte[] get(HttpRequest httpRequest) {
        return HttpResponse.error(HttpError.FORBIDDEN);
    }

    @Override
    public byte[] post(HttpRequest httpRequest) {
        return HttpResponse.error(HttpError.FORBIDDEN);
    }

    @Override
    public byte[] error(HttpError httpError) {
        return HttpResponse.error(httpError);
    }
}
