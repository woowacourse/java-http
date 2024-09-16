package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class NotFoundController extends AbstractController{

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        response.addFileBody("/404.html");
    }

    @Override
    protected void doPost(Request request, Response response) throws Exception {
    }
}
