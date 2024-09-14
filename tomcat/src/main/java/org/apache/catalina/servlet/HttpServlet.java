package org.apache.catalina.servlet;

import java.io.IOException;
import org.apache.coyote.http.request.HttpServletRequest;
import org.apache.coyote.http.response.HttpServletResponse;

public abstract class HttpServlet implements Servlet {

    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.isGet()) {
            doGet(request, response);
        }
        if (request.isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    }
}
