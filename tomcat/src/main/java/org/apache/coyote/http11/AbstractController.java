package org.apache.coyote.http11;

import org.apache.coyote.Controller;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isSameHttpMethod(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }

        if (request.isSameHttpMethod(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }

        throw new IllegalArgumentException("지원하지 않는 HttpMethod 입니다.");
    }

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws Exception;

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;
}
