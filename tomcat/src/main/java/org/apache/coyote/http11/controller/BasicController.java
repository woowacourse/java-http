package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.controller.controllermapping.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;


@RequestMapping(uri = "/")
public class BasicController extends AbstractController {
    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setCharSet("utf-8");
        response.setContentType("text/html");
        response.setResponseStatus("200");
        response.setResponseBody("Hello world!");
        response.flush();
    }
}
