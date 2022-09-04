package nextstep.jwp.controller;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    protected abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;

    protected abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;
}
