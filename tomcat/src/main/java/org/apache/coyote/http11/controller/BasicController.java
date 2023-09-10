package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.controller.controllermapping.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.resource.CharSet;
import org.apache.coyote.http11.resource.ContentType;
import org.apache.coyote.http11.resource.ResponseStatus;
import org.apache.coyote.http11.response.HttpResponse;


@RequestMapping(uri = "/")
public class BasicController extends AbstractController {
    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setCharSet(CharSet.UTF_8);
        response.setContentType(ContentType.TEXT_HTML);
        response.setResponseStatus(ResponseStatus.OK);
        response.setResponseBody("Hello world!");
        response.flush();
    }
}
