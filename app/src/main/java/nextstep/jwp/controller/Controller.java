package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import nextstep.jwp.handler.request.HttpRequest;
import nextstep.jwp.handler.response.HttpResponse;

public interface Controller {

    void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException, URISyntaxException;
}
