package nextstep.jwp.controller;

import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class HomeController extends Controller {

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return HttpResponse.ok()
                .textBody("Hello world!")
                .build();
    }
}
