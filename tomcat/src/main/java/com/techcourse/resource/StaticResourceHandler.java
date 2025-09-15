package com.techcourse.resource;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.TextResource;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

import java.io.IOException;
import java.io.UncheckedIOException;

public class StaticResourceHandler extends HttpRequestHandler {

    protected final String resourcePath;

    public StaticResourceHandler(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public String getSupportedUrl() {
        return resourcePath;
    }

    @Override
    protected HttpResponse handleGet(HttpRequest request) {
        ResponseBody responseBody = ResponseBody.fromTextResource(getResource());
        return HttpResponse.http11Builder(HttpStatus.OK)
                .body(responseBody)
                .build();
    }

    @Override
    protected HttpResponse handlePost(HttpRequest request) {
        return null;
    }

    private TextResource getResource() {
        try {
            return TextResource.fromPath("static" + resourcePath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
