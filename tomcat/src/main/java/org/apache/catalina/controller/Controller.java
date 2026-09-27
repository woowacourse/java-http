package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {
    void service(final HttpRequest request, final HttpResponse response) throws Exception;
}
