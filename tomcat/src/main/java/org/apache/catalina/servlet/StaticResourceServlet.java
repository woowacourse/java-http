package org.apache.catalina.servlet;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class StaticResourceServlet extends HttpServlet {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        super.doPost(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        try {
            String path = request.getPathWithoutQueryString();

            response.setStatus(HttpStatus.OK);
            response.setBody(path);
        } catch (IllegalArgumentException exception) {
            response.sendRedirect("/404.html");
        }
    }
}
