package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HomePageController extends Controller {

    @Override
    public HttpResponse getResponse(HttpRequest httpRequest) throws IOException {
        return HttpResponse.createWithBody(HttpStatus.OK, httpRequest.getRequestLine());
    }
}
