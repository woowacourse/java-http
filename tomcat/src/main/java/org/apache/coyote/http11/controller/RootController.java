package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;

import java.io.IOException;

public class RootController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        try {
            final StaticResource staticResource = StaticResource.from(request.getRequestLine().getPath());
            final ResponseBody responseBody = ResponseBody.from(staticResource);
            response.setResponseBody(responseBody);
            response.setHttpStatus(HttpStatus.OK);
            response.setResponseHeaders(responseBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
