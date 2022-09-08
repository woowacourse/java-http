package org.apache.catalina.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        if (isGetRequest(request)) {
            return doGet(request);
        }
        return doPost(request);
    }

    protected boolean isGetRequest(HttpRequest request) {
        return request.getHttpMethod().equals("GET");
    }

    protected abstract HttpResponse doGet(HttpRequest request);

    protected abstract HttpResponse doPost(HttpRequest request);
}
