package com.techcourse.controller;

import java.io.IOException;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.StaticResourceBody;

public class StaticResourceController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return HttpResponse.ok(StaticResourceBody.from(request.path()));
    }
}
