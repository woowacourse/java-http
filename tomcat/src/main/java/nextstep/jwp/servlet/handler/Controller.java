package nextstep.jwp.servlet.handler;

import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse2;
import org.apache.coyote.support.HttpMethod;

public abstract class Controller {

    public void service(HttpRequest request, HttpResponse2 response) {
        if (request.isMethodOf(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.isMethodOf(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        throw new UnsupportedOperationException("API not implemented");
    }

    protected abstract void doGet(HttpRequest request, HttpResponse2 response);

    protected abstract void doPost(HttpRequest request, HttpResponse2 response);
}
