package com.techcourse.servlet.static_resource;

import com.techcourse.servlet.StaticResourceServlet;

public class ChartPieJsServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/assets/chart-pie.js";
    }
}
