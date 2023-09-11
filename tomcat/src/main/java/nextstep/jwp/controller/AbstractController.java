package nextstep.jwp.controller;

import org.apache.catalina.Handler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.coyote.http11.request.RequestMethod.GET;
import static org.apache.coyote.http11.request.RequestMethod.POST;

public abstract class AbstractController implements Handler {
    @Override
    public void handle(final HttpRequest request, final HttpResponse response) throws Exception {
        RequestMethod requestMethod = request.getRequestLine().getRequestMethod();
        if (requestMethod == GET) {
            doGet(request, response);
            return;
        }
        if (requestMethod == POST) {
            doPost(request, response);
            return;
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }
}
