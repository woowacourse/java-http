package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class HelloController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        final String responseBody = "Hello world!";
        return new HttpResponse(HttpStatus.OK, responseBody, ContentType.HTML);
    }
}