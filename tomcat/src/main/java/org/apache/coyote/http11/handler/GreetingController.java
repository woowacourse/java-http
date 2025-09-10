package org.apache.coyote.http11.handler;

import java.util.LinkedHashMap;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.controller.AbstractController;

public class GreetingController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        HttpResponse actualResponse = new HttpResponse(HttpStatus.OK, new LinkedHashMap<>(), request.getHttpCookie(),
                "Hello world!".getBytes());
        response.setHttpResponse(actualResponse);
    }
}
