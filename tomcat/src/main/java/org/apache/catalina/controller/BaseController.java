package org.apache.catalina.controller;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;

public class BaseController extends AbstractController {

    @Override
    protected void doPost(final Request request, final Response response) throws Exception {
        response.setStatusLine(new StatusLine("HTTP/1.1", "200", "OK"));
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        response.setStatusLine(new StatusLine("HTTP/1.1", "200", "OK"));
        response.setBody(ResponseBody.form(request.getRequestLine()));
        response.addContentType();
        response.addContentLength();
    }
}
