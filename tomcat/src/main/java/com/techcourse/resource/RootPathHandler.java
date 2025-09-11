package com.techcourse.resource;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

import java.nio.charset.StandardCharsets;

public class RootPathHandler extends HttpRequestHandler {

    @Override
    public String getSupportedUrl() {
        return "/";
    }

    @Override
    protected HttpResponse handleGet(HttpRequest request) {
        ResponseBody responseBody = new ResponseBody(
                "Hello world!".getBytes(StandardCharsets.UTF_8), MimeType.TEXT_HTML);
        return HttpResponse.http11Builder(HttpStatus.OK)
                .body(responseBody)
                .build();
    }

    @Override
    protected HttpResponse handlePost(HttpRequest request) {
        return null;
    }
}
