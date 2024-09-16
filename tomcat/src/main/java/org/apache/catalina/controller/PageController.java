package org.apache.catalina.controller;

import org.apache.catalina.ResourceManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import java.io.IOException;

import static org.apache.coyote.http11.Status.OK;

public class PageController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        HttpResponse httpResponse = new HttpResponse();

        String responseBody = ResourceManager.getFileResource(httpRequest.getPath());

        httpResponse.setStatusLine(OK);
        httpResponse.setContentType(httpRequest.getContentType());
        httpResponse.setResponseBody(responseBody);
        httpResponse.setContentLength(responseBody.getBytes().length);

        return httpResponse;
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        return null;
    }
}
