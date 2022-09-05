package org.apache.coyote.core.controller;

import java.io.IOException;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response)
            throws IOException, UncheckedServletException {

    }

    protected void doPost(final HttpRequest request, final HttpResponse response)
            throws IOException, UncheckedServletException { /* sNOOP */ }

    protected void doGet(final HttpRequest request, final HttpResponse response)
            throws IOException, UncheckedServletException { /* NOOP */ }
}
