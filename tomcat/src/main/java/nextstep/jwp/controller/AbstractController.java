package nextstep.jwp.controller;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.StaticResource;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private static final String METHOD_NOT_ALLOWED_PATH = "/index.html";

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.isSameMethod(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.isSameMethod(HttpMethod.POST)) {
            doPost(httpRequest, httpResponse);
            return;
        }
        sendMethodNotAllowed(httpResponse);
    }

    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        sendMethodNotAllowed(httpResponse);
    }

    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        sendMethodNotAllowed(httpResponse);
    }

    private void sendMethodNotAllowed(HttpResponse httpResponse) {
        httpResponse.sendError(HttpStatus.METHOD_NOT_ALLOWED, StaticResource.path(METHOD_NOT_ALLOWED_PATH));
    }
}
