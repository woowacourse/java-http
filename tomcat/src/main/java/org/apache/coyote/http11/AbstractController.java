package org.apache.coyote.http11;

import nextstep.jwp.exception.NotSupportedHttpMethodException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.isGet()) {
            doGet(request, response);
            return;
        }
        if (request.isPost()) {
            doPost(request, response);
            return;
        }
        throw new NotSupportedHttpMethodException();
    }

    protected void doPost(HttpRequest request, HttpResponse response) { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) { /* NOOP */ }
}
