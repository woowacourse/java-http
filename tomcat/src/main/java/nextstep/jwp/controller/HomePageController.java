package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;

public class HomePageController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return HttpResponseBuilder.ok(request);
    }
}
