package nextstep.jwp.controller;

import nextstep.jwp.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.HttpStatus;

public class DefaultController extends AbstractController {
    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final var responseBody = "Hello world!";
        response.status(HttpStatus.OK)
                .contentType(ContentType.HTML)
                .body(responseBody);
    }
}
