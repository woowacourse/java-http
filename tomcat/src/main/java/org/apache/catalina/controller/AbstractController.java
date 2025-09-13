package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpMethod.POST;


public abstract class AbstractController implements Controller {

    @Override
    public void service(Request request, Response response) throws Exception {
        String method = request.getHttpMethod();
        if (GET.name().equalsIgnoreCase(method)) {
            doGet(request, response);
        } else if (POST.name().equalsIgnoreCase(method)) {
            doPost(request, response);
        } else {
            response.setHttpStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED);
            response.addHeader("Content-Length", "0");
        }
    }

    protected void doPost(Request request, Response response) throws Exception{}
    protected void doGet(Request request, Response response) throws Exception{}
}
