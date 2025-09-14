package org.apache.catalina.controller;

import com.techcourse.exception.MethodNotAllowedException;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(Http11Request request, Http11Response response) throws Exception {
        final HttpMethod method = request.getMethod();
        if (method.equals(HttpMethod.GET)) {
            doGet(request, response);
        } else if (method.equals(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        throw new MethodNotAllowedException("GET method not allowed");
    }

    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        throw new MethodNotAllowedException("POST method not allowed");
    }
}
