package com.techcourse.servlet_impl.static_resource;

import com.java.servlet.StaticResourceServlet;

public class IndexPageServlet extends StaticResourceServlet {

    @Override
    protected String resourcePath() {
        return "/index.html";
    }
}
