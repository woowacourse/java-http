package org.apache.catalina.controller;

import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public class RootController extends AbstractController {

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        response.sendResource("/index.html");
    }
}
