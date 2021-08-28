package nextstep.jwp.controller;

import nextstep.jwp.model.http.HttpProtocol;
import nextstep.jwp.model.http.HttpRequest;
import nextstep.jwp.model.http.HttpResponse;

import java.io.IOException;

import static nextstep.jwp.model.http.HttpHeaderType.CONTENT_LENGTH;
import static nextstep.jwp.model.http.HttpHeaderType.CONTENT_TYPE;
import static nextstep.jwp.model.http.MediaType.HTML;

public class DefaultController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String body = "Hello world!";
        response.addProtocol(HttpProtocol.OK);
        response.addHeader(CONTENT_TYPE, HTML.value());
        response.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.forwardBody(body);
    }
}
