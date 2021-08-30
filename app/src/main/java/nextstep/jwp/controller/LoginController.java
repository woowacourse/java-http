package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class LoginController extends AbstractController {

    public LoginController(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public byte[] get(HttpRequest httpRequest) throws IOException {
        return HttpResponse.ok(httpRequest.resource());
    }

    @Override
    public byte[] post(HttpRequest httpRequest) {
        return new byte[0];
    }

    @Override
    public byte[] error(HttpError httpError) {
        return HttpResponse.error(HttpError.FORBIDDEN);
    }
}
