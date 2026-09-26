package org.apache.catalina;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
            return;
        }
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
