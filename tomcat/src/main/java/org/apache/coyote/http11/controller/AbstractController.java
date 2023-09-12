package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;

public abstract class AbstractController implements Controller {

    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String CHARSET_UTF_8 = ";charset=utf-8";
    protected static final String CONTENT_LENGTH = "Content-Length";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.getHttpMethod() == HttpMethod.POST) {
            doPost(request, response);
        }
        if (request.getHttpMethod() == HttpMethod.GET) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }
}
