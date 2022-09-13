package org.apache.coyote.handler;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public String service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.isGet()) {
            return doGet(httpRequest, httpResponse);
        }
        return doPost(httpRequest, httpResponse);
    }

    protected String doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        throw new UncheckedServletException(null);
    }

    protected String doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        throw new UncheckedServletException(null);
    }
}
