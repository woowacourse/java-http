package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public class IndexController extends AbstractController {

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        response.putHeader("Content-Type", "text/html; charset=utf-8");
        response.putBody(response.readFileFromClasspath("index.html"));
    }
}
