package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.util.FileReader;

public class ResourceController extends AbstractController {
    private static final FileReader fileReader = new FileReader();

    private static final String HTTP_VERSION_1_1 = "HTTP/1.1";

    public ResourceController() {
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        if (request.getPath().equals("/")) {
            return HttpResponse.of(HTTP_VERSION_1_1, StatusCode.OK, "Hello world!")
                .addHeader("Content-Type", "text/html;charset=utf-8")
                .addHeader("Content-Length", "12");
        }
        return fileReader.readFile(request.getPath(), HTTP_VERSION_1_1);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        throw new IllegalArgumentException("지원하지 않는 http method입니다.");
    }
}
