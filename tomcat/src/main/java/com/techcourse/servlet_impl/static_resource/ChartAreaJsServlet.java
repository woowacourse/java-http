package com.techcourse.servlet_impl.static_resource;

import com.java.servlet.StaticResourceServlet;

public class ChartAreaJsServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/assets/chart-area.js";
    }
}
