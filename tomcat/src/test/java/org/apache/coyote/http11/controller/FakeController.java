package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FakeController extends AbstractController {

    private boolean getCalled;
    private boolean postCalled;

    @Override
    protected void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) {
        getCalled = true;
    }

    @Override
    protected void doPost(
            final HttpRequest request,
            final HttpResponse response
    ) {
        postCalled = true;
    }

    public boolean isGetCalled() {
        return getCalled;
    }

    public boolean isPostCalled() {
        return postCalled;
    }
}
