package org.apache.catalina.servlet;

import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public interface Servlet {

    void service(HttpRequest request, HttpResponse response) throws IOException;
}
