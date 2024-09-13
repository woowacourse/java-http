package org.apache.coyote.http11;

import org.apache.coyote.Controller;
import org.apache.coyote.component.HttpMethod;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        if (request.hasMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (request.hasMethod(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected abstract void doPost(HttpRequest request, HttpResponse response);

    protected abstract void doGet(HttpRequest request, HttpResponse response);
}
