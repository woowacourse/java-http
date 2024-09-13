package com.techcourse.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod requestedMethod = request.getHttpMethod();
        if (requestedMethod.equals(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (requestedMethod.equals(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void redirect(HttpResponse response, String path) {
        response.setHttpStatusCode(HttpStatusCode.FOUND);
        response.setHttpResponseHeader("Location", path);
    }

    protected void setResponseContent(HttpRequest request, HttpResponse response)
            throws URISyntaxException, IOException {
        FileReader fileReader = FileReader.getInstance();
        response.setHttpResponseBody(fileReader.readFile(request.getHttpRequestPath()));
        response.setHttpResponseHeader("Content-Type", request.getContentType() + ";charset=utf-8");
        response.setHttpResponseHeader("Content-Length",
                String.valueOf(response.getHttpResponseBody().body().getBytes().length));
    }
}
