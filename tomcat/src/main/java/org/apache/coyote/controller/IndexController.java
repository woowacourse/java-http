package org.apache.coyote.controller;

import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.Header;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class IndexController extends AbstractController {

    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }

    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setHeader(Header.CONTENT_TYPE.value(), ContentType.HTML.getMimeType());
        response.setBody(readStaticResource("/hello.html"));
    }
}
