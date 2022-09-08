package nextstep.jwp.controller;

import static org.apache.coyote.http11.message.common.ContentType.HTML;

import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class HelloWorldController extends AbstractController {

    @Override
    protected HttpResponse handleGet(final HttpRequest request) {
        return new HttpResponse.Builder()
                .contentType(HTML)
                .body("Hello world!")
                .build();
    }
}
