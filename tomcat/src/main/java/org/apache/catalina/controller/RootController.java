package org.apache.catalina.controller;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class RootController extends AbstractController {

    @Override
    public Http11Response doGet(final Http11Request request) {
        return Http11Response.ok("text/html;charset=utf-8", "Hello world!");
    }
}
