package com.techcourse.controller;

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
            response.ok();
            response.setContentType(FileType.HTML);
            response.setContentOfPlainText(HELLO_WORLD);
        }
        if (url.endsWith(HTML_SUFFIX)) {
            response.ok();
            response.setContentType(FileType.HTML);
            response.setContentOfResources(url);
        }
        if (url.endsWith(CSS_SUFFIX)) {
            response.ok();
            response.setContentType(FileType.CSS);
            response.setContentOfResources(url);
        }
        if (url.endsWith(JS_SUFFIX)) {
            response.ok();
            response.setContentType(FileType.JAVASCRIPT);
            response.setContentOfResources(url);
        }
    }
}
