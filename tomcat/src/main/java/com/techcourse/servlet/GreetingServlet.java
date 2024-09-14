package com.techcourse.servlet;

import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http.request.HttpServletRequest;
import org.apache.coyote.http.response.HttpServletResponse;

public class GreetingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.ok("Hello world!", "plain");
    }
}
