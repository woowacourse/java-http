package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;

public class UnauthorizedController extends AbstractController {

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        throw new IllegalArgumentException();
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        try {
            return HttpResponse.unauthorized(httpRequest)
                    .staticResource("/401.html")
                    .build();
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
