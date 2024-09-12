package com.techcourse.handler;

import java.io.IOException;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceHandler extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String pathWithExtension = request.getPathWithExtension();

        response.setStaticResourceResponse(pathWithExtension);
        response.write();
    }
}
