package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;


public abstract class AbstractController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        final HttpMethod method = request.getMethod();
        if (method == HttpMethod.GET) {
            this.doGet(request, response);
            return;
        }
        if (method == HttpMethod.POST) {
            this.doPost(request, response);
            return;
        }
        throw new IllegalArgumentException("지원하지 않는 Http 메소드입니다. method: " + method);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected HttpCookie makeHttpCookie(final HttpRequest httpRequest) {
        if (httpRequest.hasCookie()) {
            return HttpCookie.from(httpRequest.getCookie());
        }
        return HttpCookie.empty();
    }
}
