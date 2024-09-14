package org.apache.catalina.servlet;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Servlet {

    boolean canService(HttpRequest request);

    void service(HttpRequest request, HttpResponse response) throws IOException;
}
