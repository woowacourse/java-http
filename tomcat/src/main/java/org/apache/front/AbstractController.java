package org.apache.front;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.exception.MethodMappingFailException;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.isGet()) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.isPost()) {
            doPost(httpRequest, httpResponse);
            return;
        }
        throw new MethodMappingFailException();
    }

    protected void doPost(HttpRequest request, HttpResponse response) { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) { /* NOOP */ }
}
