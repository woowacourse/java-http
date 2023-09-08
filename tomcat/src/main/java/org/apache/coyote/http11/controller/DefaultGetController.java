package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatus;

public class DefaultGetController extends AbstractController {


    @Override
    public boolean isSupported(HttpRequest request) {
        return request.isGET() && request.isSamePath("/");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        String responseBody = "Hello world!";

        HttpResponseHeader responseHeader = new HttpResponseHeader(
                getContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length), null, null);
        return HttpResponse.of(HttpResponseStatus.OK, responseHeader, responseBody);
    }
}
