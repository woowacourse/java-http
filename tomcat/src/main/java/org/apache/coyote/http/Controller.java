package org.apache.coyote.http;

public interface Controller {

    HttpResponse doService(HttpRequest httpRequest);
}
