package org.apache.catalina.mapping;

import org.apache.coyote.util.request.HttpRequest;
import org.apache.coyote.util.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getMethod().equals("GET")) {
            doGet(request, response);
            return;
        }
        if (request.getMethod().equals("POST")) {
            doPost(request, response);
            return;
        }
        response.setStatus(org.apache.coyote.util.response.HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatus(org.apache.coyote.util.response.HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatus(org.apache.coyote.util.response.HttpStatus.METHOD_NOT_ALLOWED);
    }
}
