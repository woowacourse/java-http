package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class RegisterController implements Controller {

    @Override
    public void get(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        httpResponse.transfer(httpRequest.getUrl());
    }

    @Override
    public void post(final HttpRequest httpRequest, final HttpResponse httpResponse) {

    }
}
