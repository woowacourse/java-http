package com.techcourse.controller;

import com.techcourse.http.HttpRequest;
import com.techcourse.http.HttpResponse;
import com.techcourse.http.MimeType;
import org.apache.coyote.http11.AbstractController;

public class HelloController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setBody("Hello world!")
                .setContentType(MimeType.HTML.getMimeType());
    }
}
