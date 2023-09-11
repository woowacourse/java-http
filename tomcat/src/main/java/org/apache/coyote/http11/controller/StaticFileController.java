package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.exception.UnsupportedRequestException;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;

public class StaticFileController extends AbstractController {

    @Override
    protected void doPost(Request request, Response response) throws Exception {
        throw new UnsupportedRequestException();
    }

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        Response createdResponse = Response.createByTemplate(request.getRequestLine().getRequestURI());
        response.setBy(createdResponse);
    }
}
