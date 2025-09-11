package org.apache.catalina;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestMethod;

public abstract class AbstractController implements Controller {

    @Override
    public final void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getRequestMethod() == RequestMethod.GET) {
            doGet(request, response);
            return;
        }

        if (request.getRequestMethod() == RequestMethod.POST) {
            doPost(request, response);
            return;
        }

        throw new IllegalArgumentException("Unsupported HTTP method");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException("Unsupported HTTP method");
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        throw new UnsupportedOperationException("Unsupported HTTP method");
    }
}
