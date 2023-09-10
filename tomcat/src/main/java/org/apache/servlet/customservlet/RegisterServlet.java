package org.apache.servlet.customservlet;

import java.io.IOException;
import org.apache.coyote.http11.MemberService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseUtil;
import org.apache.servlet.ServletException;
import org.apache.servlet.SimpleHttpServlet;
import org.apache.servlet.SimpleWebServlet;

@SimpleWebServlet("/register")
public class RegisterServlet extends SimpleHttpServlet {

    @Override
    public void doGet(
            HttpRequest request, HttpResponse response) throws ServletException, IOException {
        ResponseUtil.buildStaticFileResponse(response, "/register.html");
    }

    @Override
    public void doPost(
            HttpRequest request, HttpResponse response) throws ServletException, IOException {
        validRequestContainsFormData(request);
        MemberService.register(request, response);
    }


    private void validRequestContainsFormData(HttpRequest request) {
        if (!request.containsHeader("Content-Type") && !request.getHeader("Content-Type")
                .contains("application/x-www-form-urlencoded")) {
            throw new ServletException();
        }
    }
}
