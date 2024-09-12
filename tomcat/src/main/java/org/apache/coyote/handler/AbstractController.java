package org.apache.coyote.handler;

import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
            return;
        }

        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }

        throw new UnsupportedOperationException("지원하지 않는 HTTP Method 입니다.");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
