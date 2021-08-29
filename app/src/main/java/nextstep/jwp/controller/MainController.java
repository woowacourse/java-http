package nextstep.jwp.controller;

import nextstep.jwp.model.httpMessage.HttpProtocol;
import nextstep.jwp.model.httpMessage.request.HttpRequest;
import nextstep.jwp.model.httpMessage.HttpResponse;

import java.io.IOException;

import static nextstep.jwp.model.httpMessage.HttpHeaderType.CONTENT_LENGTH;
import static nextstep.jwp.model.httpMessage.HttpHeaderType.CONTENT_TYPE;
import static nextstep.jwp.model.httpMessage.ContentType.HTML;

public class MainController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String body = "Hello world!";
        response.addProtocol(HttpProtocol.OK);
        response.addHeader(CONTENT_TYPE, HTML.value());
        response.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.forwardBody(body);
    }
}
