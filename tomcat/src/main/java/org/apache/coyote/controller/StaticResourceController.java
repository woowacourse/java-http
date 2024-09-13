package org.apache.coyote.controller;

import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class StaticResourceController extends AbstractController {

    private static final String URI = ".";
    private static final String DEFAULT_PATH = "static";

    public StaticResourceController() {
        super(URI);
    }

    @Override
    public boolean canControl(HttpRequest request) {
        return HttpMethod.GET == request.getMethod();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = DEFAULT_PATH + request.getUri();

        response.setStatusLine(HttpStatus.OK);
        response.setStaticBody(path);
    }
}
