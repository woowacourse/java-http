package org.apache.coyote.http11;

import java.net.URI;

interface ResourceHandler {

    boolean canHandle(HttpRequest httpRequest);

    HttpResponse handle(HttpRequest httpRequest);
}
