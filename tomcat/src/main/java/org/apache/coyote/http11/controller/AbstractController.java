package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Http11RequestMethod;
import org.apache.coyote.http11.response.Http11Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(Http11Request request, Http11Response response) throws Exception {
        Http11RequestMethod method = request.getMethod();
        if (method == Http11RequestMethod.GET) {
            doGet(request, response);
        }
        if (method == Http11RequestMethod.POST) {
            doPost(request, response);
        }
    }

    protected void doPost(Http11Request request, Http11Response response) throws Exception { /* NOOP */ }
    protected void doGet(Http11Request request, Http11Response response) throws Exception { /* NOOP */ }
}
