package nextstep.jwp.controller;

import nextstep.jwp.exception.MethodNotAllowedException;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.support.Request;
import org.apache.coyote.support.RequestInfo;
import org.apache.coyote.support.Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final Request request, final Response response) {
        final RequestInfo requestInfo = request.getRequestInfo();
        if (requestInfo.sameHttpMethod(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        doGet(request, response);
    }

    protected void doGet(final Request request, final Response response) {
        throw new MethodNotAllowedException();
    }

    protected void doPost(final Request request, final Response response) {
        throw new MethodNotAllowedException();
    }
}
