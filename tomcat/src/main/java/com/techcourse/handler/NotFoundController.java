package com.techcourse.handler;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class NotFoundController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.setContentType(ContentType.HTML);
        response.setHttpStatus(HttpStatus.NOT_FOUND);
        response.setResponseBody(readResource("404.html"));
    }
}
