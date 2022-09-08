package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        return HttpResponse.ok(httpRequest.getRequestURL().getPath(), "Hello world!");
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        return HttpResponse.notFound();
    }
}
