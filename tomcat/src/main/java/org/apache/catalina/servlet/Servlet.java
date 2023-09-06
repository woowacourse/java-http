package org.apache.catalina.servlet;

import java.io.IOException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Servlet {

    void service(HttpRequest request, HttpResponse response) throws IOException;
}
