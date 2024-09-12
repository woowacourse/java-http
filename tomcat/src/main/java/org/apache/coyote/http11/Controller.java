package org.apache.coyote.http11;

public interface Controller {

    void service(HttpRequest request, HttpResponseNew response);
}
