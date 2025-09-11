package com.techcourse.handler;

import com.techcourse.http.common.ContentType;
import com.techcourse.http.common.HttpCookie;
import com.techcourse.http.common.HttpVersion;
import com.techcourse.http.request.HttpRequest;
import com.techcourse.http.response.HttpResponse;
import com.techcourse.http.response.ResponseBody;

public class RootRequestHandler extends AbstractRequestHandler {

    private final HttpVersion httpVersion;

    public RootRequestHandler(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.ok(httpVersion, ContentType.TEXT_HTML, HttpCookie.empty(), ResponseBody.helloWorld());
    }
}
