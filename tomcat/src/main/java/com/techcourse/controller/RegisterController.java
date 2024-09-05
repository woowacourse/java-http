package com.techcourse.controller;

import com.techcourse.util.StaticResourceManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController {
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String STATIC_RESOURCE_PATH = "static/register.html";

    public HttpResponse doGet(HttpRequest httpRequest) {
        return new HttpResponse(200, "OK")
                .addHeader("Content-Type", "text/html")
                .setBody(StaticResourceManager.read(STATIC_RESOURCE_PATH));
    }
}
