package org.apache.coyote;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final Request request, final Response response) throws Exception {
        if (request.getMethod().equals(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }

        if (request.getMethod().equals(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected abstract void doPost(Request request, Response response) throws Exception;
    protected abstract void doGet(Request request, Response response) throws Exception;
}
