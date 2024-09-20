package org.apache.coyote.http11;

public interface Servlet {

    void service(HttpRequest req, HttpResponse resp);
}
