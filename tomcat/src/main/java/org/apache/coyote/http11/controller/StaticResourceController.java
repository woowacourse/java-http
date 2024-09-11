package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.utils.Constants;

public class StaticResourceController implements Controller {

    private static final String EMPTY_URI = "/";

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
            return new HttpResponse(Constants.DEFAULT_URI, HttpStatusCode.OK);
        }
        return new HttpResponse(uri, HttpStatusCode.OK);
    }
}
