package com.techcourse.servlet;

public class ChartAreaJsServlet extends StaticResourceServlet {

    @Override
    protected String uri() {
        return "/assets/chart-area.js";
    }

    @Override
    protected String resourcePath() {
        return "static/assets/chart-area.js";
    }

    @Override
    protected StaticResourceType type() {
        return StaticResourceType.JAVASCRIPT;
    }
}
