package org.apache.catalina.handler;

import java.io.IOException;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

public interface ResourceHandler {

    boolean canHandle(HttpRequest request);

    HttpResponse handle(HttpRequest request) throws IOException;
}
