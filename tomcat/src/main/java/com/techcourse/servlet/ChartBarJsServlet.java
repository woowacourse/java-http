package com.techcourse.servlet;

public class ChartBarJsServlet extends StaticResourceServlet {

    @Override
    protected String uri() {
        return "/assets/chart-bar.js";
    }

    @Override
    protected String resourcePath() {
        return "static/assets/chart-bar.js";
    }

    @Override
    protected StaticResourceType type() {
        return StaticResourceType.JAVASCRIPT;
    }
}
