package org.apache.coyote.common.controller;

import org.apache.coyote.common.request.Request;
import org.apache.coyote.common.response.Response;

public class AbstractController implements Controller {

    @Override
    public void service(final Request request, final Response response) throws Exception {
        if (request.isGetRequest()) {
            doGet(request, response);
            return;
        }
        doPost(request, response);
    }

    protected void doPost(Request request, Response response) throws Exception {
    }

    protected void doGet(Request request, Response response) throws Exception {
    }
}
