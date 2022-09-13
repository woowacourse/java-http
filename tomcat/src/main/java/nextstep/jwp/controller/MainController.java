package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;

public class MainController extends AbstractController {

    private static final String WELCOME_MESSAGE = "Hello World!";

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        return HttpResponse.notFound().build();
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.ok()
                .addResponseBody(WELCOME_MESSAGE, ContentType.TEXT_HTML_CHARSET_UTF_8)
                .build();
    }
}
