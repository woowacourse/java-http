package com.controller;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public class RootController extends AbstractController {

    @Override
    public Http11Response doGet(final Http11Request request) {
        return Http11Response.ok("text/html;charset=utf-8", "Hello world!");
    }
}
