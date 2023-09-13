package org.apache.coyote.handler;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        final HttpMethod method = request.getMethod();

        if (method.equals(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (method.equals(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) {
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) {
    }
}
