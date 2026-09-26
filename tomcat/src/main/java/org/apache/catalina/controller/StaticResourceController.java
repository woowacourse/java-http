package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.catalina.StaticResourceResponder;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController extends AbstractController {

    private final StaticResourceResponder resourceResponder = new StaticResourceResponder();

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return resourceResponder.serve(request.getPath());
    }
}
