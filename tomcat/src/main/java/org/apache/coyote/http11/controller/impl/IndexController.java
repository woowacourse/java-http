package org.apache.coyote.http11.controller.impl;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.ViewResolver;
import org.apache.coyote.http11.controller.AbstractController;

import java.nio.file.Files;
import java.nio.file.Path;

public final class IndexController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setHttpStatus(HttpStatus.OK);
        final ViewResolver viewResolver = new ViewResolver(Path.of(request.getRequestURI()));
        response.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.of(viewResolver.getFileExtension()).getValue());
        response.write(Files.readString(viewResolver.getResourcePath()));
    }
}
