package org.apache.catalina.controller;

import org.apache.coyote.http11.response.ResponseContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseHeaderType;

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

        response.setHttpVersion(request.getHttpVersion())
                .setStatusCode(HttpStatusCode.FOUND)
                .addHeader(ResponseHeaderType.CONTENT_TYPE, ResponseContentType.TEXT_HTML.getType())
                .addHeader(ResponseHeaderType.LOCATION, "/404.html");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {}
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {}
}
