package org.apache.catalina.controller;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpMethod;

public abstract class AbstractController implements Controller {

    @Override
    public void service(
            final Http11Request request,
            final Http11Response response
    ) throws Exception {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
        } else if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    protected void doPost(
            final Http11Request request,
            final Http11Response response
    ) throws Exception {
        // 405 Method Not Allowed
    }

    protected void doGet(
            final Http11Request request,
            final Http11Response response
    ) throws Exception {
        // 405 Method Not Allowed
    }
}
