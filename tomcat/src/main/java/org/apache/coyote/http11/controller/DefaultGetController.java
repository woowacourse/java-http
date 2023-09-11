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
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String responseBody = "Hello world!";

        HttpResponseHeader responseHeader = new HttpResponseHeader.Builder(
                readContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length))
                .build();
        response.updateResponse(HttpResponseStatus.OK, responseHeader, responseBody);
    }
}
