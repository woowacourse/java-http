package nextstep.jwp.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RootController extends Controller {

    @Override
    public HttpResponse doGet(final HttpRequest httpRequest) {
        return null;
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        return null;
    }
}
