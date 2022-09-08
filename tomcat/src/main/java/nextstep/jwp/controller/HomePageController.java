package nextstep.jwp.controller;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HomePageController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return HttpResponse.createWithBody(HttpStatus.OK, request.getRequestLine());
    }
}
