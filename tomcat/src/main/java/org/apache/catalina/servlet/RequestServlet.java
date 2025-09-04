package org.apache.catalina.servlet;

import java.io.IOException;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;

public interface RequestServlet {

    void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
