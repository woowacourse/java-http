package org.apache.coyote;

import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;

public class AbstractHandler implements Handler {

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        /* NOOP */
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
        /* NOOP */
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
        /* NOOP */
    }
}

