package nextstep.jwp.controller;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        if (request.methodEquals(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (request.methodEquals(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        response.setStatusCode(StatusCode.NOT_FOUND)
                .setRedirect("/404.html");
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) {
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) {
    }
}
