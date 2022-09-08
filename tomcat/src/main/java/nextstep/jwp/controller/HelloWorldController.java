package nextstep.jwp.controller;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.StatusCode;
import org.apache.controller.AbstractController;

public class HelloWorldController extends AbstractController {

    @Override
    protected HttpResponse handleGet(HttpRequest request) {
        return HttpResponse.of(StatusCode.OK, ContentType.TEXT_PLAIN, "Hello world!");
    }
}
