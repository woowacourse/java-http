package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        // http method 분기문
    }

    protected HttpResponse doPost(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */
        return null;
    }

    protected HttpResponse doGet(HttpRequest request, HttpResponse response) throws Exception { /* NOOP */
        return null;
    }
}
