package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

public class RootApiController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.ok(ContentType.HTML, "Hello world!");
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new IllegalArgumentException("처리할 수 없는 요청입니다.");
    }
}
