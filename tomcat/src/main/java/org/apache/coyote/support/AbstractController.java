package org.apache.coyote.support;

import org.apache.coyote.http11.http.HttpMethod;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isEqualToMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (request.isEqualToMethod(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */ }
}
