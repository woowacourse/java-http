package org.apache.catalina.controller;

import org.apache.catalina.ResourceManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import java.io.IOException;

import static org.apache.coyote.http11.Status.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.Status.OK;

public class PageController extends AbstractController {

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            String responseBody = ResourceManager.getFileResource(httpRequest.getPath());

            httpResponse.setStatusLine(OK);
            httpResponse.setContentType(httpRequest.getContentType());
            httpResponse.setResponseBody(responseBody);
            httpResponse.setContentLength(responseBody.getBytes().length);
        } catch (IOException exception) {
            httpResponse.setStatusLine(INTERNAL_SERVER_ERROR);
        }
    }
}
