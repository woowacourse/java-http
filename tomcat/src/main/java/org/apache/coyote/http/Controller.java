package org.apache.coyote.http;

public interface Controller {

    HttpResponse doService(HttpRequest httpRequest);

    boolean isMatch(HttpRequest httpRequest);
}
