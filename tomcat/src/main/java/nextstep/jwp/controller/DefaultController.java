package nextstep.jwp.controller;

import org.apache.catalina.handler.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class DefaultController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return new HttpResponse.Builder(request).ok()
            .messageBody(getStaticResource(request.getUrl())).build();
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        throw new IllegalArgumentException("Unsupported Method for this path: " + request.getPath());
    }
}
