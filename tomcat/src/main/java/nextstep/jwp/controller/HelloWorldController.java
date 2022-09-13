package nextstep.jwp.controller;

import org.apache.http.ContentType;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusCode;
import org.apache.controller.AbstractController;

public class HelloWorldController extends AbstractController {

    @Override
    protected HttpResponse handleGet(HttpRequest request) {
        return HttpResponse.of(StatusCode.OK, ContentType.TEXT_PLAIN, "Hello world!");
    }
}
