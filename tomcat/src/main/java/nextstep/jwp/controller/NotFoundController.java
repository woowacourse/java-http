package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.http11.Request.HttpRequest;
import org.apache.coyote.http11.Response.HttpResponse;
import org.apache.coyote.http11.controller.AbstractController;

public final class NotFoundController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        return HttpResponse.notFound();
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        return HttpResponse.notFound();
    }
}
