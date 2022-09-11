package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;

public class RootController extends AbstractController {
    private static final String HTTP_VERSION_1_1 = "HTTP/1.1";
    private static final String ROOT_RESPONSE_BODY = "Hello world!";
    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";

    public RootController() {
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        int contentLength = ROOT_RESPONSE_BODY.getBytes().length;

        return HttpResponse.of(HTTP_VERSION_1_1, StatusCode.OK, ROOT_RESPONSE_BODY)
            .addHeader("Content-Type", TEXT_HTML_CHARSET_UTF_8)
            .addHeader("Content-Length", Integer.toString(contentLength));
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        throw new IllegalArgumentException("지원하지 않는 http method입니다.");
    }
}
