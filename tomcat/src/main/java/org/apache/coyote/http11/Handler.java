package org.apache.coyote.http11;

public interface Handler {

    HttpResponse handle(HttpRequest httpRequest);
}
