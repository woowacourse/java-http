package org.apache.coyote.controller;

import org.apache.coyote.http11.httpmessage.request.Request;
import org.apache.coyote.http11.httpmessage.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    protected static final Logger log = LoggerFactory.getLogger(Controller.class);

    @Override
    public void service(Request request, Response response) throws Exception {
        if (request.isPostMethod()) {
            doPost(request, response);
            return;
        }
        doGet(request, response);
    }

    protected void doPost(Request request, Response response) throws Exception {}

    protected void doGet(Request request, Response response) throws Exception {}

}
