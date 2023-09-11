package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;

public class DefaultController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final String path = request.getRequestLine().getPath();
        final StaticResource staticResource = StaticResource.from(path);
        final ResponseBody responseBody = ResponseBody.from(staticResource);

        response.setHttpStatus(HttpStatus.OK);
        response.setResponseBody(responseBody);
    }
}
