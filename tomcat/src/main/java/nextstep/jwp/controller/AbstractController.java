package nextstep.jwp.controller;

import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpMethod.POST;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.exception.methodnotallowed.MethodNotAllowedException;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getHttpMethod() == GET) {
            doGet(request, response);
            return;
        }
        if (request.getHttpMethod() == POST) {
            doPost(request, response);
            return;
        }
        throw new MethodNotAllowedException();
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }
}
