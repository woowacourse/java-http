package nextstep.jwp.presentation;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.content.ContentType;
import nextstep.jwp.http.response.status.HttpStatus;

public class HelloWorldController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {

        String resource = "Hello world!";

        response.setStatusLine(request.getProtocolVersion(), HttpStatus.OK);
        response.addResponseHeader("Content-Type", ContentType.HTML.getType());
        response.addResponseHeader("Content-Length", String.valueOf(resource.getBytes().length));
        response.setResponseBody(resource);
    }
}
