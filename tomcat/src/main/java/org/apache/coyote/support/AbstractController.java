package org.apache.coyote.support;

import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public abstract class AbstractController implements Controller {

    @Override
    public Http11Response service(final Http11Request request) {
        if (request.isGetMethod()) {
            return doGet(request);
        }
        if (request.isPostMethod()) {
            return doPost(request);
        }
        throw new MethodNotAllowedException();
    }

    protected abstract Http11Response doGet(final Http11Request request);

    protected abstract Http11Response doPost(final Http11Request request);
}
