package org.apache.coyote;

import java.io.IOException;

public interface RequestHandler {

    boolean canHandling(HttpRequest httpRequest) throws IOException;

    HttpResponse handle(HttpRequest httpRequest) throws IOException;
}
