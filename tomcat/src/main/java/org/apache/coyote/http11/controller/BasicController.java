package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class BasicController extends Controller{
    @Override
    public void processPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        throw new RuntimeException("지원하지 않는 경로입니다.");
    }

    @Override
    public void processGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setBody("Hello world!");
    }
}
