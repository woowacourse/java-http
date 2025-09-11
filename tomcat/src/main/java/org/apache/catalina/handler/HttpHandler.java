package org.apache.catalina.handler;

import java.util.List;
import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpResponse;

public interface HttpHandler {

    void handle(HttpRequest request, HttpResponse response);

    List<String> getAllPath();
}
