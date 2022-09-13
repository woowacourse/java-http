package nextstep.jwp.controller;

import java.io.IOException;
import org.apache.coyote.request.HttpMethod;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.isSameHttpMethod(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        doPost(request, response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        throw new UnsupportedOperationException();
    }
}
