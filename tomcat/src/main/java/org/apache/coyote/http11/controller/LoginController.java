package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse1;

public class LoginController extends Controller {

    @Override
    public HttpResponse1 getResponse(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isExistQueryString()) {
            return HttpResponse1.from(HttpStatus.FOUND, "/index");
        }

        return HttpResponse1.of(HttpStatus.OK, httpRequest.getRequestUri());
    }
}
