package org.apache.coyote.routing;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

@FunctionalInterface
public interface Controller {
    String handle(HttpRequest request, HttpResponse response);
}
