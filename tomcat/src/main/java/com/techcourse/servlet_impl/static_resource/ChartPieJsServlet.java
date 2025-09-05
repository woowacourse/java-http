package com.techcourse.servlet_impl.static_resource;

import com.java.servlet.StaticResourceServlet;

@Deprecated
public class ChartPieJsServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/assets/chart-pie.js";
    }
}
