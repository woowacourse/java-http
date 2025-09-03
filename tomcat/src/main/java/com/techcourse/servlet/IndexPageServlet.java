package com.techcourse.servlet;

public class IndexPageServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/index.html";
    }
}
