package com.techcourse.servlet;

public class ScriptsJsServlet extends StaticResourceServlet {

    @Override
    protected String uri() {
        return "/js/scripts.js";
    }

    @Override
    protected String resourcePath() {
        return "static/js/scripts.js";
    }

    @Override
    protected StaticResourceType type() {
        return StaticResourceType.JAVASCRIPT;
    }
}
