package nextstep.jwp.controller;

import nextstep.jwp.model.http.*;

import java.io.IOException;

import static nextstep.jwp.model.http.ContentType.HTML;
import static nextstep.jwp.model.http.HTTPHeaders.CONTENT_LENGTH;
import static nextstep.jwp.model.http.HTTPHeaders.CONTENT_TYPE;

public class DefaultController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String body = "Hello world!";
        response.addProtocol(HTTPProtocol.OK);
        response.addHeader(CONTENT_TYPE, HTML.value());
        response.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.forwardBody(body);
    }
}
