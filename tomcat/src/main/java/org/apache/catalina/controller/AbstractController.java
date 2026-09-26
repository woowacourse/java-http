package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        HttpMethod method = httpRequest.getMethod();
        if (method == HttpMethod.GET) {
            doGet(httpRequest, httpResponse);
        }
        if (method == HttpMethod.POST) {
            doPost(httpRequest, httpResponse);
        }
        throw new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

    protected HttpResponse doPost(HttpRequest request, HttpResponse httpResponse) throws Exception {
        throw new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

    protected HttpResponse doGet(HttpRequest request, HttpResponse httpResponse) throws Exception {
        throw new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }
}
