package org.apache.coyote.http11;

public interface Controller {

    HttpResponse handle(HttpRequest request);
}
