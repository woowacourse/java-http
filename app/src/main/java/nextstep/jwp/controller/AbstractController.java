package nextstep.jwp.controller;

import java.io.FileNotFoundException;

import nextstep.jwp.handler.HttpRequest;
import nextstep.jwp.handler.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws FileNotFoundException {
        if (httpRequest.isGetRequest()) {
            doGet(httpRequest, httpResponse);
        }

        if (httpRequest.isPostRequest()) {
            doPost(httpRequest, httpResponse);
        }
    }

    protected abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws FileNotFoundException;

    protected abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws FileNotFoundException;
}
