package org.apache.servlet;

import java.io.IOException;
import org.apache.coyote.http11.request.SimpleServletRequest;
import org.apache.coyote.http11.response.SimpleServletResponse;

public interface SimpleServlet {

    void service(SimpleServletRequest request, SimpleServletResponse response) throws ServletException, IOException;
}
