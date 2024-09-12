package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class IndexController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        String responseBody = response.generateResponseBody("static" + request.getPath());
        response.generate200Response(path, responseBody);
    }
}
