package nextstep.jwp.controller;

import org.apache.coyote.exception.PageNotFoundException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class HomeController extends AbstractController {
    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        throw new PageNotFoundException(request.getPath());
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setBody("Hello world!");
    }
}
