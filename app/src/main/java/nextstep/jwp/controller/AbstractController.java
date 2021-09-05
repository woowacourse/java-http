package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (httpRequest.isPost()) {
            doPost(httpRequest, httpResponse);
        } else {
            doGet(httpRequest, httpResponse);
        }
    }

    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        httpResponse.transfer(httpRequest.getUrl());
    }

    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
    }
}
