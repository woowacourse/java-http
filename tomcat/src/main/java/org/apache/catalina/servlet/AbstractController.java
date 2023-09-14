package org.apache.catalina.servlet;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isPost()) {
            doPost(request, response);
            return;
        }
        if (request.isGet()) {
            doGet(request, response);
            return;
        }

        throw new UncheckedServletException();
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UncheckedServletException();
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UncheckedServletException();
    }
}
