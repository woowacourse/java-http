package org.apache.catalina.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class DefaultController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return null;
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        throw new IllegalArgumentException("Unsupported Method for this path: \"/\"");
    }
}
