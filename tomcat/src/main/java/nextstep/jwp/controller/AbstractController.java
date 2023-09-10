package nextstep.jwp.controller;

import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        if (request.isGet()) {
            doGet(request, response);
            return;
        }
        if (request.isPost()) {
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
