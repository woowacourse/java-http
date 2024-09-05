package org.apache.coyote;

public interface RequestHandler {

    boolean canHandling(HttpRequest httpRequest);

    HttpResponse handle(HttpRequest httpRequest);
}
