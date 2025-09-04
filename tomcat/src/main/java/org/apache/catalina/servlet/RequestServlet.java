package org.apache.catalina.servlet;

import org.apache.catalina.domain.HttpResponse;
import java.io.IOException;
import org.apache.catalina.domain.HttpRequest;

public interface RequestServlet {

    void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
