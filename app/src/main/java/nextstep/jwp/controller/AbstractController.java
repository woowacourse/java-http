package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (httpRequest.getHttpMethod().equals(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
        } else if (httpRequest.getHttpMethod().equals(HttpMethod.POST)) {
            doPost(httpRequest, httpResponse);
        }
    }

    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        httpResponse.transfer(httpRequest.getUrl());
    }

    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
    }
}
