package com.techcourse.servlet.static_resource;

import com.techcourse.servlet.StaticResourceServlet;

public class IndexPageServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/index.html";
    }
}
