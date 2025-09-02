package com.techcourse.servlet;

public class ChartPieJsServlet extends StaticResourceServlet {

    @Override
    protected String uri() {
        return "/assets/chart-pie.js";
    }

    @Override
    protected String resourcePath() {
        return "static/assets/chart-pie.js";
    }

    @Override
    protected StaticResourceType type() {
        return StaticResourceType.JAVASCRIPT;
    }
}
