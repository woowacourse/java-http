package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusCode;

public class RegisterController implements Controller {

    @Override
    public HttpResponse process(HttpRequest request) {
        String uri = request.getUri();
        return HttpResponse.of(uri + ".html", HttpStatusCode.OK);
    }
}
