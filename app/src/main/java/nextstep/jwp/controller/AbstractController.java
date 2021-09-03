package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

import java.io.IOException;

public class AbstractController implements Controller {
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if ("GET".equals(httpRequest.getMethod())) {
            doGet(httpRequest, httpResponse);
        }
        if ("POST".equals(httpRequest.getMethod())) {
            doPost(httpRequest, httpResponse);
        }
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        throw new IllegalArgumentException();
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        throw new IllegalArgumentException();
    }
}
