package org.apache.coyote.http11.controller;

import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        /* NOOP */
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        /* NOOP */
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        /* NOOP */
    }
}

