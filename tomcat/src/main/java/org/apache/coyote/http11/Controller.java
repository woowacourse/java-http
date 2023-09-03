package org.apache.coyote.http11;

import java.io.OutputStream;

public interface Controller {

    void handle(HttpRequest request, OutputStream outputStream);
}
