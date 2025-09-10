package com.techcourse.controller;

import com.techcourse.util.ResourceParser;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public class ResourceController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        final String path = httpRequest.getPath();

        return HttpResponse.status(HttpStatus.OK)
                .build(path, ResourceParser.parse(path));
    }
}
