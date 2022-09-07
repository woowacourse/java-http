package org.apache.coyote.support;

import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpRequest;

public class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.getMethod().isGet()) {
            doGet(request, response);
        }
        if (request.getMethod().isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
