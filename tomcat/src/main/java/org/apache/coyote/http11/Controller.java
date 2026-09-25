package org.apache.coyote.http11;

import java.io.IOException;

public interface Controller {
    HttpResponse service(HttpRequest request) throws IOException;
}
