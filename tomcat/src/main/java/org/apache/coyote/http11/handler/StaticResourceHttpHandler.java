package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class StaticResourceHttpHandler extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setBodyFromStaticResource(request.getUrlPath());
    }
}
