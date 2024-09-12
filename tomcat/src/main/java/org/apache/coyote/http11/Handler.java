package org.apache.coyote.http11;

import java.io.IOException;

public interface Handler {
    HttpResponse doHandle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
