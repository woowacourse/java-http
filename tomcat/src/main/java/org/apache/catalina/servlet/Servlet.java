package org.apache.catalina.servlet;

import java.io.IOException;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public interface Servlet {

    void doService(HttpRequest request, HttpResponse response) throws IOException;
}
