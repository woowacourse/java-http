package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public abstract class AbstractController implements Controller {


    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.isRequestOf(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.isRequestOf(HttpMethod.POST)) {
            doPost(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.isRequestOf(HttpMethod.PUT)) {
            doPut(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.isRequestOf(HttpMethod.DELETE)) {
            doDelete(httpRequest, httpResponse);
            return;
        }
        throw new UnsupportedOperationException(
            "해당 HttpMethod 는 아직 지원하지 않습니다." + httpRequest.getRequestLine().getMethod());
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.responseFrom(ResponseEntity.notAllowed());
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.responseFrom(ResponseEntity.notAllowed());
    }

    protected void doPut(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.responseFrom(ResponseEntity.notAllowed());
    }

    protected void doDelete(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.responseFrom(ResponseEntity.notAllowed());
    }
}
