package com.techcourse.controller;

import org.apache.catalina.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestMethod;

import java.net.URL;
import java.nio.file.Path;

public class RegisterGetRequestController implements Controller {

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.GET &&
                httpRequest.getRequestUrl()
                        .equals("/register");
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        URL resource = getClass().getClassLoader()
                .getResource("static/register.html");
        Path resourcePath = Path.of(resource.getPath());

        httpResponse.ok()
                .writeStaticResource(resourcePath);
    }
}
