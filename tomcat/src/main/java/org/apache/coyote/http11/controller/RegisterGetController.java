package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatus;

import java.net.URL;

public class RegisterGetController extends AbstractController {


    @Override
    public boolean isSupported(HttpRequest request) {
        return request.isGET() && request.isSamePath("/register");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        URL filePathUrl = getClass().getResource("/static/register.html");
        String responseBody = getHtmlFile(filePathUrl);

        HttpResponseHeader responseHeader = new HttpResponseHeader(
                getContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length), null, null);
        return HttpResponse.of(HttpResponseStatus.OK, responseHeader, responseBody);
    }
}
