package com.techcourse.controller;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.http11.constant.ResourcePath;
import org.apache.coyote.util.FileReader;

public class StaticResourceController extends AbstractController {

    public StaticResourceController() {
        super("static");
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        final ResourcePath resourcePath = httpRequest.getRequestLine().resourcePath();
        httpResponse.setStatusCode(HttpStatus.OK);
        httpResponse.setContentType(resourcePath.extractContentType());
        httpResponse.setBody(FileReader.readFile(resourcePath.value()));
    }
}
