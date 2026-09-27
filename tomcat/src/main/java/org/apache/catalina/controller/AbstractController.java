package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpStatusCode;

public abstract class AbstractController implements Controller {

    @Override
    public String service(HttpRequest httpRequest) throws Exception {
        HttpMethod method = httpRequest.getMethod();
        if (method == HttpMethod.GET) {
            return doGet(httpRequest);
        }
        if (method == HttpMethod.POST) {
            return doPost(httpRequest);
        }
        throw new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

    protected String doPost(HttpRequest httpRequest) throws Exception {
        throw new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

    protected String doGet(HttpRequest httpRequest) throws Exception {
        throw new HttpException(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }
}
