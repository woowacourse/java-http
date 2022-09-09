package nextstep.jwp.controller;

import nextstep.jwp.http.Request;
import nextstep.jwp.http.RequestInfo;
import nextstep.jwp.http.Response;
import org.apache.http.HttpMethod;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final Request request, final Response response) {
        final RequestInfo requestInfo = request.getRequestInfo();
        if (requestInfo.sameHttpMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (requestInfo.sameHttpMethod(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected void doGet(final Request request, final Response response) {
    }

    protected void doPost(final Request request, final Response response) {
    }
}
