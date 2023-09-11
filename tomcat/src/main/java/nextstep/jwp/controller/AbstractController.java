package nextstep.jwp.controller;

import static org.apache.coyote.request.Method.GET;
import static org.apache.coyote.request.Method.POST;

import org.apache.coyote.exception.MethodNotAllowedException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.Method;
import org.apache.coyote.response.HttpResponse;

public abstract class AbstractController implements Controller {
    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        final Method method = request.getMethod();
        if (method == GET) {
            doGet(request, response);
            return;
        }
        if (method == POST) {
            doPost(request, response);
            return;
        }
        throw new MethodNotAllowedException(request.getPath(), request.getMethod());
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) {
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) {

    }
}
