package org.apache.catalina;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        switch (request.getMethod()) {
            case GET -> doGet(request, response);
            case POST -> doPost(request, response);
            default -> response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
