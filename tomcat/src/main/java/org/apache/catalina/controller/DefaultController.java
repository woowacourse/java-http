package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;

public class DefaultController extends AbstractController {

    @Override
    protected String doPost(HttpRequest httpRequest) throws Exception {
        return null;
    }

    @Override
    protected String doGet(HttpRequest httpRequest) throws Exception {
        return null;

    }
}
