package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isMethod(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.isMethod(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
