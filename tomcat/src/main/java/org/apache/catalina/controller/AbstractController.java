package org.apache.catalina.controller;

import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StatusCode;

public abstract class AbstractController implements Controller {

    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String CHARSET_UTF_8 = ";charset=utf-8";
    protected static final String CONTENT_LENGTH = "Content-Length";
    private static final String ALLOW = "Allow";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.getHttpMethod() == HttpMethod.POST) {
            doPost(request, response);
            return;
        }
        if (request.getHttpMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        response.setStatusCode(StatusCode.METHOD_NOT_ALLOWED)
            .addHeader(ALLOW, "GET, POST");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }
}
