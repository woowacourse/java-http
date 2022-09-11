package nextstep.jwp.controller;

import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.http11.header.HttpVersion;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Location;

public class HomeController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return new HttpResponse(request.getHttpVersion(), HttpStatus.OK, Location.empty(), request.getContentType(),
                "Hello world!");
    }
}
