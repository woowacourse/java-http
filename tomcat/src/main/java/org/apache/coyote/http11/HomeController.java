package org.apache.coyote.http11;

import static org.apache.coyote.http11.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.HttpStatus.OK;

public class HomeController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.status(OK)
                .body("Hello world!")
                .contentType(TEXT_HTML)
                .build();
    }
}
