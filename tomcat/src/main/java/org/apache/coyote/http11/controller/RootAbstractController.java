package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.config.TomcatServerConfiguration.DEFAULT_CHARSET_NAME;

import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;

public class RootAbstractController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setHeader("Content-Type", "text/html;charset=" + DEFAULT_CHARSET_NAME);
        response.setBody("Hello world!");
    }

    @Override
    public boolean canHandle(HttpRequest request) {
            return request.getRequestLine().getPath().equals("/");
    }
}
