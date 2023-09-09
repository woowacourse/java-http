package org.apache.coyote.http11;

public interface Controller {

    HttpResponse service(HttpRequest request);
}
