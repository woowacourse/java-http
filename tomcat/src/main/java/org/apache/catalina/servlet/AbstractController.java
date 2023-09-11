package org.apache.catalina.servlet;

import org.apache.catalina.exception.MethodNotSupportedException;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        if (request.isRequestMethodOf(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }

        if (request.isRequestMethodOf(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        throw new MethodNotSupportedException();
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new MethodNotSupportedException();
    }
}
