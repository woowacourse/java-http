package nextstep.jwp.controller;

import nextstep.jwp.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class ExceptionController implements Controller {
    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        createErrorResponse(response);
    }

    protected abstract void createErrorResponse(final HttpResponse response);
}
