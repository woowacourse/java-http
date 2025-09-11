package com.techcourse.controller;

import com.techcourse.controller.core.AbstractRequestController;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpVersion;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.ResponseBody;

public class RootRequestController extends AbstractRequestController {

    private final HttpVersion httpVersion;

    public RootRequestController(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.ok(httpVersion, ContentType.TEXT_HTML, HttpCookie.empty(), ResponseBody.helloWorld());
    }
}
