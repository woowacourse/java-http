package org.apache.catalina.controller;

import static org.apache.catalina.controller.StaticResourceUri.NOT_FOUND_PAGE;
import static org.apache.coyote.http11.response.ResponseContentType.TEXT_HTML;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public abstract class AbstractController implements Controller {

    static final String RESOURCE_DIRECTORY = "static";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpRequestMethod requestMethod = request.getMethod();

        if (requestMethod == HttpRequestMethod.POST) {
            doPost(request, response);
            return;
        }

        if (requestMethod == HttpRequestMethod.GET) {
            doGet(request, response);
            return;
        }

        response.setStatusCode(HttpStatusCode.FOUND)
                .addContentTypeHeader(TEXT_HTML.getType())
                .addLocationHeader(NOT_FOUND_PAGE.getUri());
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }
}
