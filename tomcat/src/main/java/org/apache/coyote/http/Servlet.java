package org.apache.coyote.http;

public interface Servlet {

    HttpResponse doService(HttpRequest httpRequest);

    boolean isMatch(HttpRequest httpRequest);
}
