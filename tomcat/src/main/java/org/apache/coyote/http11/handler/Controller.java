package org.apache.coyote.http11.handler;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public interface Controller {

    boolean support(HttpRequest request);

    void handle(HttpRequest request, HttpResponse response);
}
