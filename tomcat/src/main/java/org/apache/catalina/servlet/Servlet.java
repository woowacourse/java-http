package org.apache.catalina.servlet;

import java.io.IOException;
import org.apache.coyote.http.request.HttpServletRequest;
import org.apache.coyote.http.response.HttpServletResponse;

public interface Servlet {

    void doService(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
