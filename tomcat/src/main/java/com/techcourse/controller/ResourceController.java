package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.FileType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController extends AbstractController {
    public static final String HELLO_WORLD = "Hello world!";
    public static final String HTML_SUFFIX = ".html";
    public static final String CSS_SUFFIX = ".css";
    public static final String JS_SUFFIX = ".js";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final var url = request.getRequestPath();

        if ("/".equals(url)) {
            buildIndexUrlResponse(response);
        }
        if (url.endsWith(HTML_SUFFIX)) {
            buildResourceResponse(response, FileType.HTML, url);
        }
        if (url.endsWith(CSS_SUFFIX)) {
            buildResourceResponse(response, FileType.CSS, url);
        }
        if (url.endsWith(JS_SUFFIX)) {
            buildResourceResponse(response, FileType.JAVASCRIPT, url);
        }
    }

    private void buildIndexUrlResponse(HttpResponse response) {
        response.ok();
        response.setContentType(FileType.HTML);
        response.setContentOfPlainText(HELLO_WORLD);
    }

    private void buildResourceResponse(HttpResponse response, FileType fileType, String url) throws IOException {
        response.ok();
        response.setContentType(fileType);
        response.setContentOfResources(url);
    }
}
