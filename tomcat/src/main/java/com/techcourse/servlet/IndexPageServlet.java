package com.techcourse.servlet;

public class IndexPageServlet extends StaticResourceServlet {

    @Override
    protected String uri() {
        return "/index.html";
    }

    @Override
    protected String resourcePath() {
        return "static/index.html";
    }

    @Override
    protected StaticResourceType type() {
        return StaticResourceType.HTML;
    }
}
