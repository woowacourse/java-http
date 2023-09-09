package org.apache.catalina;

import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    public HttpResponse service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.methodEquals("GET")) {
            return doGet(httpRequest, httpResponse);
        }
        if (httpRequest.methodEquals("POST")) {
            return doPost(httpRequest, httpResponse);
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP Method 입니다.");
    }

    public HttpResponse doGet(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException();
    }

    public HttpResponse doPost(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException();
    }
}
