package com.techcourse.controller;

import java.io.IOException;
import org.apache.coyote.http11.Servlet;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.response.HttpServletResponse;

public class GreetingServlet implements Servlet {

    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.ok("Hello world!", "plain");
    }
}
