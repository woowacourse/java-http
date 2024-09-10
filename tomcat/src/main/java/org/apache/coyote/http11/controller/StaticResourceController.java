package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.httpresponse.HttpStatusCode;

public class StaticResourceController implements Controller {

    private static final String EMPTY_URI = "/";
    private static final String DEFAULT_URI = "/index.html";

    private static StaticResourceController instance = new StaticResourceController();

    private StaticResourceController() {
    }

    public static Controller getInstance() {
        return instance;
    }

    @Override
    public HttpResponse process(HttpRequest request) {
        String uri = request.getUri();
        if (EMPTY_URI.equals(uri)) {
            return HttpResponse.of(DEFAULT_URI, HttpStatusCode.OK);
        }
        return HttpResponse.of(uri, HttpStatusCode.OK);
    }
}
