package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import nextstep.jwp.handler.request.HttpRequest;
import nextstep.jwp.handler.response.HttpResponse;

public class ErrorController extends AbstractController {

    public static final String NOT_FOUND_HTML = "/404.html";

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException, URISyntaxException {
        httpResponse.notFound(NOT_FOUND_HTML);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException, URISyntaxException {
        httpResponse.notFound(NOT_FOUND_HTML);
    }
}
